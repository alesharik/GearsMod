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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.IntStream;

import static com.alesharik.gearsmod.steam.Calculator.*;

public class BasicSteamNetwork implements SteamNetwork, INBTSerializable<NBTTagCompound> {
    /**
     * Measures in MPa
     */
    final AtomicDouble pressure;
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
    }

    @Override
    public void addSteam(double volume, double temperature) {
        this.temperature.set(this.temperature.get() + PhysicMath.celsiusToKelvin(temperature) / 2F);
        this.pressure.addAndGet(PhysicMath.getSteamPressureForTemperature(this.temperature.get()) * volume);
        recalculatePressure(this.volume.get(), this.temperature.get(), pressure, world, poses);
    }

    @Override
    public boolean removeSteam(double volume) {
        double pressureToRemove = PhysicMath.getSteamPressureForTemperature(this.temperature.get());

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
        recalculatePressure(this.volume.get(), this.temperature.get(), pressure, world, poses);
        return true;
    }

    @Override
    public void syncTemperature(double t) {
        this.pressure.set(this.pressure.get() + PhysicMath.celsiusToKelvin(t) / 2F);
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
        if(!poses.contains(pos)) {
            addBlockPos(pos);
        }
    }

    @Override
    public void destroyBlock(BlockPos pos) {
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
        for(BlockPos blockPos : poses) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if(tileEntity == null)
                continue;

            EnumFacing[] facing;
            if(tileEntity instanceof SteamStorageProvider)
                facing = ((SteamStorageProvider) tileEntity).getConnectedFacing();
            else
                facing = calcFacing(tileEntity);

            for(EnumFacing enumFacing : facing) {
                if(blockPos.offset(enumFacing) == pos)
                    return true;
            }
        }
        return false;
    }

    void addBlockPos(BlockPos pos) {
        poses.add(pos);
        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), temperature.get(), pressure, world, poses);

        SteamNetworkHandler.markDirty(this);
    }

    void removeBlockPos(BlockPos pos) {
        poses.remove(pos);
        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), temperature.get(), pressure, world, poses);

        SteamNetworkHandler.markDirty(this);
        if(poses.size() == 0)
            SteamNetworkHandler.deleteNetwork(this);
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

        recalculateVolume(world, poses, volume);
        recalculatePressure(volume.get(), temperature.get(), pressure, world, poses);
    }
}
