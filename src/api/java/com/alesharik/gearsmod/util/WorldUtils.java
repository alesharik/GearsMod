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

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class WorldUtils {
    private WorldUtils() {
        throw new UnsupportedOperationException();
    }

    public static void changeBlockState(World world, BlockPos pos, Function<IBlockState, IBlockState> replacer) {
        TileEntity tileEntity = world.getTileEntity(pos);

        world.setBlockState(pos, replacer.apply(world.getBlockState(pos)));
        TileEntity newTileEntity = world.getTileEntity(pos);
        if(newTileEntity != null)
            newTileEntity.invalidate();

        if(tileEntity != null) {
            tileEntity.validate();
            world.setTileEntity(pos, tileEntity);
        }
    }


    @Nullable
    public static Fluid getFluid(World world, BlockPos pos) {
        IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, null);
        if(handler != null) {
            FluidStack stack = handler.drain(Fluid.BUCKET_VOLUME, false);
            return stack != null ? stack.getFluid() : null;
        }
        return null;
    }
}
