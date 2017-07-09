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

package com.alesharik.gearsmod.client.render;

import com.alesharik.gearsmod.tileEntity.temperature.ThermometerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.model.obj.OBJModel;

import javax.annotation.Nonnull;
import javax.vecmath.Vector4f;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ThermometerFastTESR extends FastTESR<ThermometerTileEntity> {
    private static final Map<Integer, IBakedModel> modelCache = new ConcurrentHashMap<>();

    static {
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> modelCache.clear());
    }

    private static IBakedModel getModel(int delta) {
        if(modelCache.containsKey(delta))
            return modelCache.get(delta);
        else {
            IBakedModel model = bakeModel(delta);
            modelCache.put(delta, model);
            return model;
        }
    }

    private static IBakedModel bakeModel(int delta) {
        OBJModel m = null;
        try {
            m = (OBJModel) ModelLoaderRegistry.getModel(new ResourceLocation("gearsmod:block/temperature/thermometer.obj"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }
        m = format(m, delta);
        return m.bake(m.getDefaultState(), DefaultVertexFormats.ITEM, input -> {
            assert input != null;
            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
        });
    }

    private static OBJModel format(OBJModel model, int delta) {
        if(delta == 0 || delta > 200)
            return model;

        float y = (float) (0.141094 + ((0.859344 - 0.141094) / 200 * delta));
        float vInvert = (float) (0.6875 + ((0.9990 - 0.6875) / 200 * delta));
//        float v = (float) (0.9990 - ((0.9990 - 0.6875) / 200 * (200 - delta)));
        float v = (float) (0.9990 - (vInvert - 0.6875));
        model.getMatLib().getGroups().forEach((s, group) -> {
            if(!OBJModel.Group.DEFAULT_NAME.equals(s)) {
                group.getFaces().forEach(face -> {
                    boolean contains = false;
                    boolean isBottom = false;
                    for(OBJModel.Vertex vertex : face.getVertices()) {
                        if(Math.abs(vertex.getPos3().getY() - 0.500219) < 0.000001) {//TODO remove
                            contains = true;
                        }
                        if(vertex.getTextureCoordinate().v == 0.703075F)
                            isBottom = true;
                    }
                    for(OBJModel.Vertex vertex : face.getVertices()) {
                        if(Math.abs(vertex.getPos3().getY() - 0.500219) < 0.000001) {
                            vertex.setPos(new Vector4f(vertex.getPos().getX(), y, vertex.getPos().getZ(), vertex.getPos().getW()));
                            if(vertex.getTextureCoordinate().v == 0.7246F || vertex.getTextureCoordinate().v == 0.703075) {
                                vertex.setTextureCoordinate(new OBJModel.TextureCoordinate(vertex.getTextureCoordinate().u, isBottom ? v : vInvert, vertex.getTextureCoordinate().w));
                            } else {
                                vertex.setTextureCoordinate(new OBJModel.TextureCoordinate(vertex.getTextureCoordinate().u, v, vertex.getTextureCoordinate().w));
                            }
                        }

                    }
                    if(contains) {
                        for(OBJModel.Vertex vertex : face.getVertices()) {
                            System.err.println(vertex.getTextureCoordinate().u + ":" + vertex.getTextureCoordinate().v);
                        }
                        System.err.println("Bottom: " + isBottom + "\n");
                    }

                });
            }
        });
        return model;
    }

    @Override
    public void renderTileEntityFast(@Nonnull ThermometerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, @Nonnull VertexBuffer buffer) {
        bindTexture(new ResourceLocation("gearsmod:textures/blocks/temperature/thermometer_map.png"));
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        double temp = te.getTemperature();
        if(temp < -45)
            temp = -45;
        if(temp > 150)
            temp = 150;

        double part = (te.getMaxTemperature()) / 200;
        IBakedModel model = getModel((int) ((temp + 45) * part));
//        try {
//            OBJModel m = (OBJModel) ModelLoaderRegistry.getModel(new ResourceLocation("gearsmod:block/temperature/thermometer.obj"));
//            model = m.bake(m.getDefaultState(), DefaultVertexFormats.ITEM, input -> {
//                assert input != null;
//                return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

//        model = dispatcher.getBlockModelShapes().getModelForState(te.getWorld().getBlockState(pos.offset(EnumFacing.NORTH)));
//        IBakedModel rotated = RotationUtils.rotateModel(model, new TransformationMatrix().beginPivotPoint(new Vector3f(0.5F, 0.5F, 0.5F)).endPivotPoint(new Vector3f(0.5F, 0.5F, 0.5F)), te.getWorld().getBlockState(pos));
        dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, te.getWorld().getBlockState(pos), pos, buffer, false);
    }
}
