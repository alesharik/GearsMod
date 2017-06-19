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

package com.alesharik.gearsmod.gui.client;

import com.alesharik.gearsmod.gui.server.BasicSteamBoilerContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public final class BasicSteamBoilerGui extends ProgressBarGuiContainer<BasicSteamBoilerContainer> {
    private static final ResourceLocation texture = new ResourceLocation("gearsmod:gui/basic_steam_boiler_gui.png");

    private int startX;
    private int startY;

    public BasicSteamBoilerGui(BasicSteamBoilerContainer inventorySlotsIn) {
        super(inventorySlotsIn);

        xSize = 256;
        ySize = 120;
    }

    @Override
    public void initGui() {
        super.initGui();

        startX = (width - xSize) / 2;
        startY = (height - ySize) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        bindTexture(texture);
        drawTexturedModalRect(startX, startY, 0, 0, 256, 120);

        IFluidTankProperties waterProperty = container.getTileEntity().getWaterProperty();
        FluidStack contents = waterProperty.getContents();
        if(contents == null) return;
        ResourceLocation location = contents.getFluid().getStill();
        location = new ResourceLocation(location.getResourceDomain(), "textures/" + location.getResourcePath() + ".png");
        bindTexture(location);
        int height = (int) (81F / 100 * (Math.max(0, contents.amount - 1000) * 1F / waterProperty.getCapacity() * 100));
        drawTexturedModalRect(startX + 9, startY + 90 - height, 0, 0, 24, height);

        int height2 = (int) (90 * (Math.min(1000, contents.amount) * 1F / waterProperty.getCapacity()));
        drawTexturedModalRect(startX + 78, startY + 73 - height2, 0, 0, 103, height2);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.enableBlend();

        bindTexture(texture);
        drawTexturedModalRect(9, 9, 0, 121, 24, 81);
        drawTexturedModalRect(225, 20, 25, 121, 28, 74);
        drawTexturedModalRect(78, 64, 54, 130, 103, 9);

        GlStateManager.disableBlend();
    }
}
