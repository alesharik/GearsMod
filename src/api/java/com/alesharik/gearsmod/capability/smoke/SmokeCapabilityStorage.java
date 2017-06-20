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

package com.alesharik.gearsmod.capability.smoke;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class SmokeCapabilityStorage implements Capability.IStorage<SmokeStorage> {
    @Nonnull
    @Override
    public NBTBase writeNBT(@Nullable Capability<SmokeStorage> capability, SmokeStorage instance, @Nullable EnumFacing side) {
        return new NBTTagInt(instance.getSmokeAmount());
    }

    @Override
    public void readNBT(@Nullable Capability<SmokeStorage> capability, @Nonnull SmokeStorage instance, @Nullable EnumFacing side, @Nonnull NBTBase nbt) {
        if(instance instanceof SmokeHandler)
            ((SmokeHandler) instance).smokeAmount = ((NBTTagInt) nbt).getInt();
        else
            throw new IllegalArgumentException("Cannot deserialize custom realisation! Try to extends from SmokeHandler, not SmokeStorage");
    }
}
