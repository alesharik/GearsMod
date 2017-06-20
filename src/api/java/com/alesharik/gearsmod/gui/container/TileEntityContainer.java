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

package com.alesharik.gearsmod.gui.container;

import com.alesharik.gearsmod.tileEntity.FieldTileEntity;
import com.alesharik.gearsmod.util.field.FieldStore;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * This container supports progress bar update by updating store fields
 *
 * @param <T> TileEntity
 */
public abstract class TileEntityContainer<T extends FieldTileEntity> extends Container {
    protected final T tileEntity;

    public TileEntityContainer(@Nonnull T tileEntity) {
        this.tileEntity = tileEntity;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        FieldStore fieldStore = tileEntity.getFieldStore();
        int old = fieldStore.getFieldOrDefault(id, 0);
        fieldStore.setField(id, data);
        listeners.forEach(listener -> listener.sendProgressBarUpdate(this, old, data));
    }

    public FieldStore getStore() {
        return tileEntity.getFieldStore();
    }

    public T getTileEntity() {
        return tileEntity;
    }
}
