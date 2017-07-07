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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.model.obj.OBJModel;

import javax.annotation.Nonnull;

public final class ThermometerFastTESR extends FastTESR<ThermometerTileEntity> {
    @Override
    public void renderTileEntityFast(@Nonnull ThermometerTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, @Nonnull VertexBuffer buffer) {
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model;
        try {
            OBJModel m = (OBJModel) ModelLoaderRegistry.getModel(new ResourceLocation("gearsmod:block/temperature/thermometer.obj"));
            m.getMatLib().getGroups().remove(OBJModel.Group.DEFAULT_NAME);
            model = m.bake(m.getDefaultState(), DefaultVertexFormats.ITEM, input -> {
                assert input != null;
                return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
//        model = dispatcher.getBlockModelShapes().getModelForState(te.getWorld().getBlockState(pos.offset(EnumFacing.NORTH)));
//        IBakedModel rotated = RotationUtils.rotateModel(model, new TransformationMatrix().beginPivotPoint(new Vector3f(0.5F, 0.5F, 0.5F)).endPivotPoint(new Vector3f(0.5F, 0.5F, 0.5F)), te.getWorld().getBlockState(pos));
        dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, te.getWorld().getBlockState(pos), pos, buffer, false);

    }
}
