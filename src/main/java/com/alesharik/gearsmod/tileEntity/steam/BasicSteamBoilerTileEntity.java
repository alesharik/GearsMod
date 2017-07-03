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
import com.alesharik.gearsmod.capability.smoke.SmokeHandlerSynchronizer;
import com.alesharik.gearsmod.capability.smoke.SmokeHandlerUpdateListener;
import com.alesharik.gearsmod.capability.steam.SteamStorage;
import com.alesharik.gearsmod.steam.SteamNetworkHandler;
import com.alesharik.gearsmod.steam.SteamStorageProvider;
import com.alesharik.gearsmod.tileEntity.FieldTileEntity;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.util.field.SimpleTileEntityFieldStore;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.alesharik.gearsmod.util.PhysicMath.*;

public final class BasicSteamBoilerTileEntity extends FieldTileEntity implements ITickable, SteamStorageProvider {
    private final SmokeHandler smokeHandler;
    private final SynchronizedFluidTank fluidHandler;
    private final ItemStackHandler coalItemStackHandler;
    private SteamStorage steamHandler;

    private volatile double lastMJ;
    private volatile double temperature; //In Celsius

    public BasicSteamBoilerTileEntity() {
        super();
        smokeHandler = new SmokeHandler(10000, false, true);
        fluidHandler = new SynchronizedFluidTank(FluidRegistry.WATER, 0, 10000);
        fluidHandler.setTileEntity(this);
        coalItemStackHandler = new ItemStackHandler();
        store = new SimpleTileEntityFieldStore(GearsMod.getNetworkWrapper());
        lastMJ = 0;
        temperature = 100;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        store.setWorld(world);
        store.setBlockPos(pos);
        store.sync();

        smokeHandler.setListener(new SmokeHandlerUpdateListener(pos, world, world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite()));
        SmokeHandlerSynchronizer.synchronize(world, pos, world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite());

        fluidHandler.setTileEntity(this);
        fluidHandler.setFacing(world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY());
        fluidHandler.sync(Side.CLIENT);

        steamHandler = SteamNetworkHandler.getStorageForBlock(world, pos, 1000, 10000, aDouble -> ModLoggerHolder.getModLogger().log(Level.ERROR, "Ok"));
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
        fluidHandler.sync();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        try {
            smokeHandler.deserializeNBT((NBTTagInt) compound.getTag("smoke"));
            NBTTagCompound fluidCompound = compound.getCompoundTag("fluid");
            fluidHandler.readFromNBT(fluidCompound);
            coalItemStackHandler.deserializeNBT(compound.getCompoundTag("coal"));
            lastMJ = compound.getDouble("lastMJ");
            temperature = compound.getDouble("temperature");
        } catch (NullPointerException e) {
            ModLoggerHolder.getModLogger().log(Level.WARN, "Fucking forge doesn't send normal NBT!");
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        NBTTagCompound compound = super.writeToNBT(c);
        compound.setTag("smoke", smokeHandler.serializeNBT());
        NBTTagCompound fluidCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidCompound);
        compound.setTag("fluid", fluidCompound);
        compound.setTag("coal", coalItemStackHandler.serializeNBT());
        compound.setDouble("lastMJ", lastMJ);
        compound.setDouble("temperature", temperature);
        return compound;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
        readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    @Nonnull
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, serializeNBT());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void dropItems() {
        ItemStack itemStack = coalItemStackHandler.getStackInSlot(0);
        if(itemStack != ItemStack.EMPTY) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack));
        }
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            if(!isWorking()) {
                double count = burnableItemToMegaJoules(coalItemStackHandler.getStackInSlot(0));
                if(count > 0) {
                    lastMJ += count;
                    setWorking(true);
                } else {
                    temperature -= 1F / 20;
                }
            } else {
                temperature += 1F / 20;
                FluidStack drain = fluidHandler.drain(1, false);
                if(fluidHandler.getFluid() == null || (drain != null && drain.amount <= 0)) {
                    setWorking(false);
                    return;
                }
                fluidHandler.drain(1, true);

                double mjRequired = MEGA_JOULES_PER_MILLI_BUCKET;
                if(lastMJ < mjRequired) {
                    setWorking(false);
                    return;
                }
                lastMJ -= mjRequired;
                steamHandler.getNetwork().addSteam(getSteamPressureForTemperature(celsiusToKelvin(temperature)), temperature);
            }
            if(temperature < 100)
                temperature = 100;
            markDirty();
        }
    }

    private boolean isWorking() {
        return world.getBlockState(pos).getValue(BlockMachine.WORKING_PROPERTY);
    }

    private void setWorking(boolean b) {
        world.setBlockState(pos, world.getBlockState(pos)
                .withProperty(BlockMachine.WORKING_PROPERTY, b));
    }

    @Nonnull
    @Override
    public SteamStorage getSteamStorage() {
        return steamHandler;
    }

    @Nonnull
    @Override
    public EnumFacing[] getConnectedFacing() {
        return new EnumFacing[]{EnumFacing.UP};
    }
}
