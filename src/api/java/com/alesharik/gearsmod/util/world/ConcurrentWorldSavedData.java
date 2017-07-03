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

package com.alesharik.gearsmod.util.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;

public abstract class ConcurrentWorldSavedData extends WorldSavedData {
    private final AtomicBoolean isDirty;
    private final StampedLock lock = new StampedLock();

    public ConcurrentWorldSavedData(String name) {
        super(name);
        isDirty = new AtomicBoolean(false);
    }

    @Override
    public void markDirty() {
        isDirty.set(true);
    }

    @Override
    public boolean isDirty() {
        return isDirty.get();
    }

    @Override
    public void setDirty(boolean isDirty) {
        this.isDirty.set(isDirty);
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        long stamp = lock.writeLock();
        try {
            super.deserializeNBT(nbt);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        long stamp = lock.tryOptimisticRead();
        try {
            NBTTagCompound compound = super.serializeNBT();
            if(!lock.validate(stamp)) {
                stamp = lock.readLock();
                compound = super.serializeNBT();
            }
            return compound;
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
