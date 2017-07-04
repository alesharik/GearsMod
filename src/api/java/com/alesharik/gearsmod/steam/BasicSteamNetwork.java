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

package com.alesharik.gearsmod.steam;

import com.alesharik.gearsmod.util.PhysicMath;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.IntStream;

import static com.alesharik.gearsmod.steam.Calculator.*;

public class BasicSteamNetwork implements SteamNetwork, INBTSerializable<NBTTagCompound> {
    /**
     * Measures in MPa
     */
    final AtomicDouble pressure;
    final AtomicDouble pressureVolume;
    /**
     * Measures in Kelvin
     */
    final AtomicDouble temperature;
    /**
     * Measures in m3
     */
    final AtomicDouble volume;

    private final Set<BlockPos> poses;
    private final World world;

    BasicSteamNetwork(World world) {
        pressure = new AtomicDouble();
        temperature = new AtomicDouble();
        poses = new CopyOnWriteArraySet<>();
        this.world = world;
        this.volume = new AtomicDouble();
        this.pressureVolume = new AtomicDouble();
    }

    @Override
    public void addSteam(double volume, double temperature) {
        syncTemperature(temperature);
        this.pressureVolume.addAndGet(volume);
        this.pressure.addAndGet(PhysicMath.getSteamPressureForTemperature(this.temperature.get()) * volume);
        recalculatePressure(this.volume.get(), pressureVolume.get(), this.temperature.get(), pressure, world, poses);
    }

    @Override
    public boolean removeSteam(double volume) {
        double pressureToRemove = PhysicMath.getSteamPressureForTemperature(this.temperature.get()) * volume;

        double pr = this.pressure.get();
        double val = pr - pressureToRemove;
        if(val < 0)
            return false;
        while(!pressure.compareAndSet(pr, val)) {
            pr = pressure.get();
            val = pr - pressureToRemove;
            if(val < 0)
                return false;
        }
        this.pressureVolume.addAndGet(-volume);
        recalculatePressure(this.volume.get(), this.pressureVolume.get(), this.temperature.get(), pressure, world, poses);
        return true;
    }

    @Override
    public void syncTemperature(double t) {
        double delta = PhysicMath.celsiusToKelvin(t) - this.temperature.get();
        this.temperature.addAndGet(delta / 2);
    }

    @Override
    public double getTemperature() {
        return temperature.get();
    }

    @Override
    public double getPressure() {
        return pressure.get();
    }

    @Override
    public void initBlock(BlockPos pos) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if(!poses.contains(pos)) {
            addBlockPos(pos);
        }
    }

    @Override
    public void destroyBlock(BlockPos pos) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;

        if(poses.contains(pos)) {
            removeBlockPos(pos);
        }
    }

    void bakeSteamStorage(SteamStorageImpl storage) {
        SteamStorageImpl.networkUpdater.set(storage, this);
    }

    boolean validBlockPos(IBlockAccess world, BlockPos pos) {
        if(poses.contains(pos))
            return true;

        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity == null)
            return false;

        EnumFacing[] facing;
        if(tileEntity instanceof SteamStorageProvider)
            facing = ((SteamStorageProvider) tileEntity).getConnectedFacing();
        else
            facing = calcFacing(tileEntity);

        for(EnumFacing enumFacing : facing) {
            BlockPos near = pos.offset(enumFacing);
            if(poses.contains(near)) {
                TileEntity nearTileEntity = world.getTileEntity(pos);
                if(nearTileEntity == null)
                    return false;

                EnumFacing[] facing1;
                if(nearTileEntity instanceof SteamStorageProvider)
                    facing1 = ((SteamStorageProvider) nearTileEntity).getConnectedFacing();
                else
                    facing1 = calcFacing(nearTileEntity);

                for(EnumFacing enumFacing1 : facing1) {
                    if(enumFacing.getOpposite() == enumFacing1)
                        return true;
                }
            }
        }

        return false;
    }

    void addBlockPos(BlockPos pos) {
        poses.add(pos);
        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), pressureVolume.get(), temperature.get(), pressure, world, poses);

        SteamNetworkHandler.markDirty(this);
        if(world instanceof WorldServer)
            Calculator.checkCorrectness((WorldServer) world, new ArrayList<>(poses));
    }

    void removeBlockPos(BlockPos pos) {
        poses.remove(pos);
        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), pressureVolume.get(), temperature.get(), pressure, world, poses);

        SteamNetworkHandler.markDirty(this);
        if(poses.size() == 0)
            SteamNetworkHandler.deleteNetwork(this);
        else if(world instanceof WorldServer)
            Calculator.checkCorrectness((WorldServer) world, new ArrayList<>(poses));
    }

    int getPoseCount() {
        return poses.size();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof BasicSteamNetwork)) return false;

        BasicSteamNetwork that = (BasicSteamNetwork) o;

        if(!pressure.equals(that.pressure)) return false;
        return getTemperature() == that.getTemperature();
    }

    @Override
    public int hashCode() {
        int result = pressure.hashCode();
        result = 31 * result + temperature.hashCode();
        return result;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("pressure", pressure.get());
        compound.setDouble("temperature", temperature.get());
        compound.setDouble("pressureVolume", pressureVolume.get());
        NBTTagList blockPoses = new NBTTagList();
        poses.stream()
                .map(BlockPos::toLong)
                .map(NBTTagLong::new)
                .forEach(blockPoses::appendTag);
        compound.setTag("blockPoses", blockPoses);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        pressure.set(nbt.getDouble("pressure"));
        temperature.set(nbt.getDouble("temperature"));
        poses.clear();
        NBTTagList blockPoses = nbt.getTagList("blockPoses", Constants.NBT.TAG_LONG);
        IntStream.range(0, blockPoses.tagCount())
                .mapToObj(blockPoses::get)
                .map(nbtBase -> (NBTTagLong) nbtBase)
                .map(NBTTagLong::getLong)
                .map(BlockPos::fromLong)
                .forEach(poses::add);
        pressureVolume.set(nbt.getDouble("pressureVolume"));
        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), pressureVolume.get(), temperature.get(), pressure, world, poses);
    }
}
