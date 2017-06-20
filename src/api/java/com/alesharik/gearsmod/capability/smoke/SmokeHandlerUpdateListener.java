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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Update SmokeHandler then it changed
 */
public final class SmokeHandlerUpdateListener implements SmokeHandler.Listener {
    private final BlockPos pos;
    private final World world;
    private final EnumFacing facing;

    public SmokeHandlerUpdateListener(BlockPos pos, World world, EnumFacing facing) {
        this.pos = pos;
        this.world = world;
        this.facing = facing;
    }

    @Override
    public void receive(int amount) {
        SmokeHandlerSynchronizer.synchronize(world, pos, facing);
    }

    @Override
    public void extract(int amount, boolean simulate) {
        if(!simulate)
            SmokeHandlerSynchronizer.synchronize(world, pos, facing);
    }

    @Override
    public void update(int amount) {
        //Do nothing
    }
}
