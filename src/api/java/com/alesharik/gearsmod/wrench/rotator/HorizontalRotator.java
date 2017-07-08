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

package com.alesharik.gearsmod.wrench.rotator;

import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.wrench.Rotator;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Works with {@link BlockHorizontal#FACING}
 */
public final class HorizontalRotator implements Rotator {
    private static final HorizontalRotator ROTATOR = new HorizontalRotator();

    private HorizontalRotator() {
    }

    public static HorizontalRotator getInstance() {
        return ROTATOR;
    }

    @Override
    public EnumFacing getNextFacing(IBlockState state) {
        return state.getValue(BlockHorizontal.FACING).rotateY();
    }

    @Override
    public void rotateBlock(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
        if(!world.getBlockState(pos).getBlock().rotateBlock(world, pos, facing))
            ModLoggerHolder.getModLogger().debug("Wat");
//        WorldUtils.changeBlockState(world, pos, state1 -> state1.withProperty(BlockHorizontal.FACING, facing));
    }
}
