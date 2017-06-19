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

import com.alesharik.gearsmod.gui.container.TileEntityContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

/**
 * @param <T> container
 */
public abstract class ProgressBarGuiContainer<T extends TileEntityContainer<?>> extends GuiContainer {
    protected final T container;

    public ProgressBarGuiContainer(T inventorySlotsIn) {
        super(inventorySlotsIn);
        this.container = inventorySlotsIn;
    }

    public int getProgressBarValue(int id) {
        return container.getStore().getField(id);
    }

    public void setProgressBarValue(int id, int data) {
        container.getStore().setField(id, data);
    }

    public void bindTexture(ResourceLocation texture) {
        mc.getTextureManager().bindTexture(texture);
    }
}
