/*
 *     This file is part of GearsMod.
 *
 *     GearsMod is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GearsMod is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GearsMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alesharik.gearsmod.util.rotation;

import com.alesharik.gearsmod.util.BakedQuadDeconstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple4f;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class RotationUtils {
    private RotationUtils() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static AxisAlignedBB rotateAABB(AxisAlignedBB aabb, EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return aabb;
            case UP:
                return new AxisAlignedBB(aabb.minX, 1 - aabb.maxY, aabb.minZ, aabb.maxX, 1 - aabb.minY, aabb.maxZ);
            case NORTH:
                return new AxisAlignedBB(aabb.minX, aabb.minZ, aabb.minY, aabb.maxX, aabb.maxZ, aabb.maxY);
            case SOUTH:
                return new AxisAlignedBB(aabb.minX, aabb.minZ, 1 - aabb.maxY, aabb.maxX, aabb.maxZ, 1 - aabb.minY);
            case WEST:
                return new AxisAlignedBB(aabb.minY, aabb.minZ, aabb.minX, aabb.maxY, aabb.maxZ, aabb.maxX);
            case EAST:
                return new AxisAlignedBB(1 - aabb.maxY, aabb.minZ, aabb.minX, 1 - aabb.minY, aabb.maxZ, aabb.maxX);
            default:
                return aabb;
        }
    }

    @SideOnly(Side.CLIENT)
    public static IBakedModel rotateModel(IBakedModel model, TransformationMatrix rotationMatrix, IBlockState state) {
        if(model instanceof OBJModel.OBJBakedModel)
            return new WrapperBakedModel(model, Stream.of(model.getQuads(state, null, 0))
                    .flatMap(Collection::stream)
                    .map(bakedQuads -> rotateQuad(bakedQuads, rotationMatrix))
                    .toArray(BakedQuad[]::new));
        return new WrapperBakedModel(model, Stream.of(EnumFacing.values())
                .flatMap(side -> model.getQuads(state, side, 0).stream())
                .map(quad -> rotateQuad(quad, rotationMatrix))
                .toArray(BakedQuad[]::new));
    }

    @SideOnly(Side.CLIENT)
    public static BakedQuad rotateQuad(BakedQuad quad, TransformationMatrix rotationMatrix) {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(quad.getFormat());
        BakedQuadDeconstructor.BakedQuadInfo bakedQuadInfo = BakedQuadDeconstructor.deconstruct(quad);

        bakedQuadInfo.getVertexInfo().forEach(vertexInfo -> {
            VertexFormat format = quad.getFormat();
            for(int i = 0; i < format.getElementCount(); i++) {
                switch (format.getElement(i).getUsage()) {
                    case POSITION: {
                        Tuple4f vertex = new Point4f(vertexInfo.getX(), vertexInfo.getY(), vertexInfo.getZ(), vertexInfo.getW());
                        rotationMatrix.transformVector(vertex);
                        builder.put(i, vertex.getX(), vertex.getY(), vertex.getZ(), vertex.getW());
                        break;
                    }
                    case UV:
                        builder.put(i, vertexInfo.getU(), vertexInfo.getV(), 0, 1);
                        break;
                    case COLOR:
                        builder.put(i, vertexInfo.getColor().getRed() / 255F, vertexInfo.getColor().getGreen() / 255F, vertexInfo.getColor().getBlue() / 255F, vertexInfo.getColor().getAlpha() / 255F);
                        break;
                    case NORMAL:
                        builder.put(i, vertexInfo.getNormalX(), vertexInfo.getNormalY(), vertexInfo.getNormalZ(), 0);
                        break;
                    default:
                        builder.put(i);
                        break;
                }
            }
        });
        builder.setApplyDiffuseLighting(quad.shouldApplyDiffuseLighting());
        builder.setQuadOrientation(quad.getFace());
        builder.setQuadTint(quad.getTintIndex());
        builder.setTexture(quad.getSprite());
        return builder.build();
    }

    private static final class WrapperBakedModel implements IBakedModel {
        private final IBakedModel wrap;
        private final List<BakedQuad> quads;

        public WrapperBakedModel(IBakedModel wrap, BakedQuad[] quads) {
            this.wrap = wrap;
            this.quads = Arrays.asList(quads);
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return quads;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean isAmbientOcclusion() {
            return wrap.isAmbientOcclusion();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean isGui3d() {
            return wrap.isGui3d();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public boolean isBuiltInRenderer() {
            return wrap.isBuiltInRenderer();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public TextureAtlasSprite getParticleTexture() {
            return wrap.getParticleTexture();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return wrap.getItemCameraTransforms();
        }

        @SideOnly(Side.CLIENT)
        @Override
        public ItemOverrideList getOverrides() {
            return wrap.getOverrides();
        }
    }
}
