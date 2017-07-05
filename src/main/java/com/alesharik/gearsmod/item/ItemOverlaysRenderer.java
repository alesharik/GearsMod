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

package com.alesharik.gearsmod.item;

import com.alesharik.gearsmod.temperature.BiomeTemperatureManager;
import com.alesharik.gearsmod.util.ScaledScreenResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.*;

//TODO add config for location
@SideOnly(Side.CLIENT)
public final class ItemOverlaysRenderer {
    private static final ResourceLocation texture = new ResourceLocation("gearsmod:textures/overlays/portable_thermometer_overlay.png");
    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

    private int lastHeight;
    private long lastTimeStamp;

    private static boolean checkPlayer(EntityPlayer player) {
        IInventory inventory = player.inventory;
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(stack.getItem() == ModItems.PORTABLE_THERMOMETER) {
                PortableThermometer.Mode mode = ModItems.PORTABLE_THERMOMETER.getMode(stack);
                if(mode == PortableThermometer.Mode.ALWAYS)
                    return true;
                else if(mode == PortableThermometer.Mode.HOLD && (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack))
                    return true;
            }
        }
        return false;
    }

    private static void renderTextureRect(int x, int y, int textureX, int textureY, int width, int height) {
        float zlevel = 200;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double) (x), (double) (y + height), (double) zlevel).tex((double) ((float) (textureX) * 0.015625F), (double) ((float) (textureY + height) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double) (x + width), (double) (y + height), (double) zlevel).tex((double) ((float) (textureX + width) * 0.015625F), (double) ((float) (textureY + height) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double) (x + width), (double) (y), (double) zlevel).tex((double) ((float) (textureX + width) * 0.015625F), (double) ((float) (textureY) * 0.0078125F)).endVertex();
        vertexbuffer.pos((double) (x), (double) (y), (double) zlevel).tex((double) ((float) (textureX) * 0.015625F), (double) ((float) (textureY) * 0.0078125F)).endVertex();
        tessellator.draw();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(!checkPlayer(minecraft.player))
            return;

        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            pushMatrix();

            long newTimeStamp = System.currentTimeMillis();
            long timeDelta = newTimeStamp - lastTimeStamp;
            int meterHeight = lastHeight;

            if(timeDelta > 50) {
                lastTimeStamp = newTimeStamp;
                BlockPos playerPosition = minecraft.player.getPosition();
                double temperature = BiomeTemperatureManager.getTemperatureManager(minecraft.world.getBiome(playerPosition), minecraft.world).getTemperatureSmart(minecraft.world, playerPosition);

                int currentMeterHeight = (int) (53 * (temperature + 33) / 133);

                if(lastHeight < currentMeterHeight)
                    meterHeight = lastHeight + 1;
                else if(lastHeight > currentMeterHeight)
                    meterHeight = lastHeight - 1;
                else
                    meterHeight = currentMeterHeight;
            }

            translate(ScaledScreenResolution.getScaledWidth() - 36, 6, 0);
            enableBlend();

            textureManager.bindTexture(texture);
            renderTextureRect(0, 0, 0, 0, 32, 64);
            renderTextureRect(12, 5 + (53 - meterHeight), 12, 69, 8, meterHeight);
            renderTextureRect(0, 0, 32, 64, 32, 64);
            renderTextureRect(0, 0, 32, 0, 32, 64);

            lastHeight = meterHeight;

            disableBlend();
            popMatrix();
        }
    }
}
