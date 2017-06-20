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

package com.alesharik.gearsmod.tileEntity;

import com.alesharik.gearsmod.util.field.FieldStore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * TileEntity with field store
 */
public class FieldTileEntity extends TileEntity {
    protected FieldStore store;

    public FieldTileEntity() {
    }

    public FieldStore getFieldStore() {
        return store;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(store != null)
            store.deserializeNBT((NBTTagCompound) compound.getTag("store"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        NBTTagCompound compound = super.writeToNBT(c);
        compound.setTag("store", store.serializeNBT());
        return compound;
    }
}
