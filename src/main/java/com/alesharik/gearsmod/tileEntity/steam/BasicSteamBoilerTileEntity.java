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

package com.alesharik.gearsmod.tileEntity.steam;

import com.alesharik.gearsmod.block.BlockMachine;
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BasicSteamBoilerTileEntity extends TileEntity {
    private final SmokeHandler smokeHandler;

    public BasicSteamBoilerTileEntity() {
        smokeHandler = new SmokeHandler(10000, false, true);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if(facing == world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite() && capability == SmokeCapability.DEFAULT_CAPABILITY) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(facing == EnumFacing.SOUTH && capability == SmokeCapability.DEFAULT_CAPABILITY)
            return (T) smokeHandler;
        else
            return null;
    }
}
