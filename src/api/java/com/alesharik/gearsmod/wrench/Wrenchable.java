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

package com.alesharik.gearsmod.wrench;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * If block implements this interface, it can be easily break by wrench. Same used for {@link net.minecraft.block.material.Material}
 */
public interface Wrenchable {
    /**
     * This method called when user tries to break block with wrench
     *
     * @param world the world
     * @param pos   block position
     * @param state block state
     */
    default void wrenchBlock(@Nonnull World world, @Nonnull BlockPos pos, IBlockState state) {
        state.getBlock().breakBlock(world, pos, state);
        state.getBlock().dropBlockAsItem(world, pos, state, 1);
        world.setBlockToAir(pos);
    }

    /**
     * This method called before wrench tries to break the block
     *
     * @param wrench the wrench
     * @return true if block can be wrenched
     */
    default boolean handleWrench(@Nonnull Wrench wrench) {
        wrench.damageWrench();
        return true;
    }
}
