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

package com.alesharik.gearsmod.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class SerializableEnergyStorage extends EnergyStorage implements INBTSerializable<NBTTagCompound> {
    public SerializableEnergyStorage(int capacity) {
        super(capacity);
    }

    public SerializableEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public SerializableEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public SerializableEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("energy", energy);
        return compound;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("energy", energy);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        energy = nbt.getInteger("energy");
        if(energy > getMaxEnergyStored())
            energy = getMaxEnergyStored();
    }
}
