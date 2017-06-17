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

package com.alesharik.gearsmod.tileEntity.smoke;

import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ChimneyTileEntity extends TileEntity implements ITickable {
    private final SmokeHandler smokeHandler;

    public ChimneyTileEntity() {
        this.smokeHandler = new SmokeHandler(2000, true, false);
    }

    @Override
    public void update() {
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return facing == EnumFacing.NORTH && capability == SmokeCapability.DEFAULT_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(facing == EnumFacing.NORTH && capability == SmokeCapability.DEFAULT_CAPABILITY)
            return (T) smokeHandler;
        return null;
    }
}
