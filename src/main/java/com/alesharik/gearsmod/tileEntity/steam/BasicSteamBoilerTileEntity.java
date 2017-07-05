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
import com.alesharik.gearsmod.temperature.BiomeTemperatureManager;
import com.alesharik.gearsmod.tileEntity.FieldTileEntity;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.util.PhysicMath;
import com.alesharik.gearsmod.util.WorldUtils;
import com.alesharik.gearsmod.util.field.SimpleTileEntityFieldStore;
import com.alesharik.gearsmod.util.provider.TemperatureProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Random;

import static com.alesharik.gearsmod.util.PhysicMath.*;

public final class BasicSteamBoilerTileEntity extends FieldTileEntity implements ITickable, SteamStorageProvider, TemperatureProvider {
    private static final Random RANDOM = new SecureRandom();

    private final SmokeHandler smokeHandler;
    private final SynchronizedFluidTank fluidHandler;
    private final ItemStackHandler coalItemStackHandler;
    private SteamStorage steamHandler;

    private volatile double lastMJ;
    private volatile double temperature; //In Celsius
    private volatile double minTemperature;

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
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            SmokeHandlerSynchronizer.synchronize(world, pos, world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite());

        fluidHandler.setTileEntity(this);
        fluidHandler.setFacing(world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY());
        fluidHandler.sync(Side.SERVER);

        steamHandler = SteamNetworkHandler.getStorageForBlock(world, pos, 1000, 1200 * 1000 * 1000, aDouble -> ModLoggerHolder.getModLogger().log(Level.ERROR, "Ok"));
        steamHandler.getNetwork().initBlock(pos);

        minTemperature = BiomeTemperatureManager.getTemperatureManager(world.getBiome(pos), world).getTemperatureSmart(world, pos);
        temperature = minTemperature;
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
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if((facing == null || facing == world.getBlockState(pos).getValue(BlockMachine.FACING).getOpposite()) && capability == SmokeCapability.DEFAULT_CAPABILITY)
            return (T) smokeHandler;
        else if((facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateY() || facing == world.getBlockState(pos).getValue(BlockMachine.FACING).rotateYCCW() || facing == null)
                && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) fluidHandler;
        else if(facing == null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) coalItemStackHandler;
        else
            return super.getCapability(capability, facing);
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
            if(smokeHandler.overloaded())
                smokeHandler.extract((int) (2 + Math.round((smokeHandler.getSmokeAmount() * 1.0F / smokeHandler.getMaxSmokeAmount()) * 1.5)), false);

            if(temperature < minTemperature)
                temperature = minTemperature;
            else if(temperature + 75 > PhysicMath.MAX_STEAM_TEMPERATURE)
                temperature = PhysicMath.MAX_STEAM_TEMPERATURE - 75;

            if(!isWorking()) {
                FluidStack drain = fluidHandler.drain(1, false);
                if(fluidHandler.getFluid() == null || (drain != null && drain.amount <= 0)) {
                    temperature -= 1F / 50;
                    steamHandler.getNetwork().syncTemperature(temperature);
                    return;
                }

                double count = burnableItemToMegaJoules(coalItemStackHandler.getStackInSlot(0));

                if(count > 0) {
                    lastMJ += count;
                    setWorking(true);
                    ItemStack itemStack = coalItemStackHandler.getStackInSlot(0);
                    itemStack.setCount(itemStack.getCount() - 1);
                } else {
                    temperature -= 1F / 50;
                    steamHandler.getNetwork().syncTemperature(temperature);
                }
            } else {
                FluidStack drain = fluidHandler.drain(1, false);
                if(fluidHandler.getFluid() == null || (drain != null && drain.amount <= 0)) {
                    setWorking(false);
                    return;
                }

                temperature += 1F / 20;
                if(temperature < 100)
                    return;

                double mjRequired = getMegaJoulesWithEfficiency(MEGA_JOULES_PER_MILLI_BUCKET, smokeHandler.getSmokeAmount(), smokeHandler.getMaxSmokeAmount());
                if(mjRequired == 0) {
                    lastMJ -= 0.01;
                    smokeHandler.receiveInternal(10);
                    return;
                }

                if(lastMJ < mjRequired) {
                    setWorking(false);
                    return;
                }

                fluidHandler.drain(10, true);

                lastMJ -= mjRequired;
                steamHandler.getNetwork().addSteam(10, temperature);

                smokeHandler.receiveInternal(10);
            }
            markDirty();
        } else {
            if(smokeHandler.overloaded()) {
                if(RANDOM.nextInt(3) == 0)
                    return;

                for(int i = 0; i < 5 + (smokeHandler.getSmokeAmount() * 1.0F / smokeHandler.getMaxSmokeAmount()) * 4; i++) {
                    double d3 = (double) pos.getX() + RANDOM.nextInt(10) / 10F + RANDOM.nextDouble() * 0.60000000149011612D;
                    double d8 = (double) pos.getY() + 0.95 + RANDOM.nextDouble();
                    double d13 = (double) pos.getZ() + RANDOM.nextInt(10) / 10F + RANDOM.nextDouble() * 0.60000000149011612D;
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    private boolean isWorking() {
        return world.getBlockState(pos).getValue(BlockMachine.WORKING_PROPERTY);
    }

    private void setWorking(boolean b) {
        WorldUtils.changeBlockState(world, pos, state -> state.withProperty(BlockMachine.WORKING_PROPERTY, b));
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

    public void onRemove() {
        steamHandler.getNetwork().destroyBlock(pos);
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public double getMaxTemperature() {
        return PhysicMath.MAX_STEAM_TEMPERATURE;
    }
}
