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

package com.alesharik.gearsmod.capability.fluid;

import com.alesharik.gearsmod.NetworkWrapperHolder;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

/**
 * Require non-null tile for work
 */
public class SynchronizedFluidTank extends FluidTank {
    private EnumFacing facing;

    public SynchronizedFluidTank(int capacity) {
        super(capacity);
    }

    public SynchronizedFluidTank(@Nullable FluidStack fluidStack, int capacity) {
        super(fluidStack, capacity);
    }

    public SynchronizedFluidTank(Fluid fluid, int amount, int capacity) {
        super(fluid, amount, capacity);
    }

    public SynchronizedFluidTank setFacing(EnumFacing facing) {
        this.facing = facing;
        return this;
    }

    @Override
    protected void onContentsChanged() {
        if(tile == null)
            return;

        int amount = fluid == null ? -1 : fluid.amount;
        Fluid liquid = fluid == null ? null : fluid.getFluid();
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            NetworkWrapperHolder.getNetworkWrapper().sendToDimension(new FluidSynchronizationMessage(tile.getPos(), tile.getWorld(), amount, facing, liquid), tile.getWorld().provider.getDimension());
        } else {
            ModLoggerHolder.getModLogger().log(Level.INFO, "Detected sync message about fluid from client to server!");
            NetworkWrapperHolder.getNetworkWrapper().sendToServer(new FluidSynchronizationMessage(tile.getPos(), tile.getWorld(), amount, facing, liquid));
        }
    }

    void setAmount(String name, int amount) {
        FluidStack fluid = this.fluid;
        if(fluid == null || !fluid.getFluid().getName().equals(name)) {
            this.fluid = new FluidStack(FluidRegistry.getFluid(name), amount);
        } else {
            this.fluid.amount = amount;
        }
    }

    void clear() {
        this.fluid = null;
    }
}
