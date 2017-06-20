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

import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.block.BlockMachine;
import com.alesharik.gearsmod.capability.fluid.SynchronizedFluidTank;
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeHandler;
import com.alesharik.gearsmod.capability.smoke.SmokeHandlerUpdateListener;
import com.alesharik.gearsmod.tileEntity.FieldTileEntity;
import com.alesharik.gearsmod.util.field.SimpleTileEntityFieldStore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BasicSteamBoilerTileEntity extends FieldTileEntity {
    private final SmokeHandler smokeHandler;
    private final SynchronizedFluidTank fluidHandler;
    private final ItemStackHandler coalItemStackHandler;

    public BasicSteamBoilerTileEntity() {
        super();
        smokeHandler = new SmokeHandler(10000, false, true);
        fluidHandler = new SynchronizedFluidTank(FluidRegistry.WATER, 0, 10000);
        fluidHandler.setTileEntity(this);
        coalItemStackHandler = new ItemStackHandler();
    }

    @Override
    public void onLoad() {
        store = new SimpleTileEntityFieldStore(pos, world, GearsMod.getNetworkWrapper());
        smokeHandler.setListener(new SmokeHandlerUpdateListener(pos, world, world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite()));

        fluidHandler.setTileEntity(this);
        fluidHandler.setFacing(world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY());

        markDirty();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if((facing == null || facing == world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite()) && capability == SmokeCapability.DEFAULT_CAPABILITY)
            return true;
        else if((facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY() || facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateYCCW() || facing == null)
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        else if(facing == null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if((facing == null || facing == EnumFacing.SOUTH) && capability == SmokeCapability.DEFAULT_CAPABILITY)
            return (T) smokeHandler;
        else if((facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY() || facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateYCCW() || facing == null)
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) fluidHandler;
        else if(facing == null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) coalItemStackHandler;
        else
            return null;
    }

    @Nonnull
    public IFluidTankProperties getWaterProperty() {
        return fluidHandler.getTankProperties()[0];
    }

    public ItemStackHandler getCoalItemStackHandler() {
        return coalItemStackHandler;
    }

    public void addLiquid(int count) {
        fluidHandler.fill(new FluidStack(FluidRegistry.WATER, count), true);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        smokeHandler.deserializeNBT((NBTTagInt) compound.getTag("smoke"));
        NBTTagCompound fluidCompound = compound.getCompoundTag("fluid");
        fluidHandler.readFromNBT(fluidCompound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        NBTTagCompound compound = super.writeToNBT(c);
        compound.setTag("smoke", smokeHandler.serializeNBT());
        NBTTagCompound fluidCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidCompound);
        compound.setTag("fluid", fluidCompound);
        return compound;
    }
}
