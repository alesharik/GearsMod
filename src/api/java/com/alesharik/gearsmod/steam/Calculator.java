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

import com.alesharik.gearsmod.capability.steam.SteamCapability;
import com.alesharik.gearsmod.capability.steam.SteamStorage;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.util.PhysicMath;
import com.alesharik.gearsmod.util.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

final class Calculator {
    private Calculator() {
        throw new UnsupportedOperationException();
    }

    static void recalculateVolume(World world, Set<BlockPos> poses, AtomicDouble store) {
        double value = 0;
        for(BlockPos pos : poses) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity == null)
                continue;

            SteamStorage steamStorage;
            if(tileEntity instanceof SteamStorageProvider) {
                steamStorage = ((SteamStorageProvider) tileEntity).getSteamStorage();
                if(steamStorage == null && tileEntity.hasCapability(SteamCapability.CAPABILITY, null)) {
                    steamStorage = tileEntity.getCapability(SteamCapability.CAPABILITY, null);
                    if(steamStorage == null)
                        continue;
                }
            } else if(tileEntity.hasCapability(SteamCapability.CAPABILITY, null)) {
                steamStorage = tileEntity.getCapability(SteamCapability.CAPABILITY, null);
                if(steamStorage == null)
                    continue;
            } else {
                continue;
            }

            if(steamStorage == null)
                continue;
            value += steamStorage.getVolume();
        }
        store.set(value);
    }

    /**
     * @param pressure in MPa
     * @param volume   in m3
     * @param world
     * @param poses
     */
    static void checkForce(double pressure, double volume, World world, Set<BlockPos> poses) {
        double force = PhysicMath.getForceByPressureAndVolume(pressure, volume);
        HandleForceTask task = new HandleForceTask(force);

        for(BlockPos pos : poses) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity == null)
                continue;

            SteamStorage steamStorage;
            if(tileEntity instanceof SteamStorageProvider) {
                steamStorage = ((SteamStorageProvider) tileEntity).getSteamStorage();
            } else if(tileEntity.hasCapability(SteamCapability.CAPABILITY, null)) {
                steamStorage = tileEntity.getCapability(SteamCapability.CAPABILITY, null);
                if(steamStorage == null)
                    continue;
            } else {
                continue;
            }

            task.add(steamStorage);
        }
        task.execute();
    }

    /**
     * @param volume
     * @param temperature in Kelvin
     * @param pressure
     * @param world
     * @param poses
     */
    static void recalculatePressure(double volume, double pressureVolume, double temperature, AtomicDouble pressure, World world, Set<BlockPos> poses) {
        double newValue = PhysicMath.getSteamPressureForTemperature(temperature) * pressureVolume;
        double last = pressure.getAndSet(newValue);
        if(!compare(newValue, last, 0.0001)) {
            checkForce(newValue, volume, world, poses);
        }
    }

    static void checkCorrectness(WorldServer world, List<BlockPos> poses) {
        Map<BlockPos, EnumFacing[]> map = new ConcurrentHashMap<>();
        ListIterator<BlockPos> iterator = poses.listIterator();
        while(iterator.hasNext()) {
            BlockPos next = iterator.next();
            TileEntity tileEntity = world.getTileEntity(next);
            if(tileEntity == null) {
                iterator.remove();
                continue;
            }
            EnumFacing[] facing;
            if(tileEntity instanceof SteamStorageProvider)
                facing = ((SteamStorageProvider) tileEntity).getConnectedFacing();
            else
                facing = calcFacing(tileEntity);
            if(facing.length == 0) {
                iterator.remove();
                continue;
            }

            map.put(next, facing);
        }
        Utils.getModForkJoinPool().execute(new CheckCorrectnessTask(map, poses, world));
    }

    private static boolean compare(double a, double b, double eps) {
        return a == b || Math.abs(a - b) < eps;
    }

    static EnumFacing[] calcFacing(TileEntity tileEntity) {
        EnumFacing[] facing = new EnumFacing[6];
        int i = 0;
        for(EnumFacing enumFacing : EnumFacing.values()) {
            if(tileEntity.hasCapability(SteamCapability.CAPABILITY, enumFacing)) {
                facing[i] = enumFacing;
                i++;
            }
        }
        EnumFacing[] ret = new EnumFacing[i];
        System.arraycopy(facing, 0, ret, 0, i);
        return ret;
    }

    private static final class HandleForceTask implements Runnable {
        private final List<SteamStorage> steamStorage;
        private final double force;

        HandleForceTask(double force) {
            this.force = force;
            steamStorage = new CopyOnWriteArrayList<>();
        }

        void execute() {
            if(steamStorage.size() > 0)
                Utils.getModForkJoinPool().execute(this);
        }

        void add(SteamStorage steamStorage) {
            if(steamStorage.getMaxForce() < force)
                return;
            this.steamStorage.add(steamStorage);
        }

        @Override
        public void run() {
            for(SteamStorage steamStorage : steamStorage) {
                steamStorage.handleOverloadForce(force);
            }
        }
    }

    private static final class CheckCorrectnessTask implements Runnable {
        private final Map<BlockPos, EnumFacing[]> poses;
        private final List<BlockPos> baseList;
        private final WorldServer world;

        public CheckCorrectnessTask(Map<BlockPos, EnumFacing[]> poses, List<BlockPos> list, WorldServer world) {
            this.poses = poses;
            this.baseList = list;
            this.world = world;
        }

        @Override
        public void run() {
            if(poses.size() == 0)
                return;
            List<BlockPos> myPoses = new ArrayList<>();

            BlockPos first = (BlockPos) poses.keySet().toArray()[0];
            myPoses.add(first);

            List<BlockPos> otherPoses = new ArrayList<>();

            for(Map.Entry<BlockPos, EnumFacing[]> entry : poses.entrySet()) {
                if(entry.getKey() == first)
                    continue;

                BlockPos[] blockPoses = getConnectedBlockPoses(entry.getKey(), entry.getValue());
                BlockPos[] realPoses = new BlockPos[blockPoses.length];
                int realPosesI = 0;
                for(BlockPos blockPose : blockPoses) {
                    if(poses.containsKey(blockPose) && checkOpposite(poses.get(blockPose), entry.getValue())) {
                        realPoses[realPosesI] = blockPose;
                        realPosesI++;
                    }
                }
                if(realPosesI > 0) {
                    boolean ok = false;
                    for(int i = 0; i < realPosesI; i++) {
                        if(myPoses.contains(realPoses[realPosesI])) {
                            myPoses.add(entry.getKey());
                            ok = true;
                        }
                    }
                    if(!ok)
                        otherPoses.add(entry.getKey());
                } else {
                    world.addScheduledTask(new UpdateNetworksTask(world, Collections.singletonList(entry.getKey()), SteamNetworkHandler.getSteamNetwork(world, entry.getKey())));
                }
            }

            if(otherPoses.size() > 0)
                Utils.getModForkJoinPool().execute(new ExtractNewNetworkFromBlocksTask(otherPoses.stream()
                        .map(pos -> Pair.of(pos, poses.get(pos)))
                        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)), world));

            baseList.clear();
            baseList.addAll(myPoses);
        }

        private BlockPos[] getConnectedBlockPoses(BlockPos pos, EnumFacing[] facing) {
            BlockPos[] ret = new BlockPos[facing.length];
            int i = 0;
            for(EnumFacing enumFacing : facing) {
                ret[i] = pos.offset(enumFacing);
                i++;
            }
            return ret;
        }

        private boolean checkOpposite(EnumFacing[] first, EnumFacing[] second) {
            for(EnumFacing enumFacing : first) {
                EnumFacing opposite = enumFacing.getOpposite();
                for(EnumFacing facing : second) {
                    if(facing == opposite)
                        return true;
                }
            }
            return false;
        }
    }

    private static final class ExtractNewNetworkFromBlocksTask implements Runnable {
        private final Map<BlockPos, EnumFacing[]> poses;
        private final WorldServer world;

        public ExtractNewNetworkFromBlocksTask(Map<BlockPos, EnumFacing[]> poses, WorldServer world) {
            this.poses = poses;
            this.world = world;
        }

        @Override
        public void run() {
            List<BlockPos> myPoses = new ArrayList<>();

            BlockPos first = (BlockPos) poses.keySet().toArray()[0];
            myPoses.add(first);

            List<BlockPos> otherPoses = new ArrayList<>();

            poses.entrySet().stream().skip(1).forEach(posEntry -> {
                BlockPos pos = posEntry.getKey();
                for(EnumFacing facing : posEntry.getValue()) {
                    BlockPos near = pos.offset(facing);
                    if(myPoses.contains(near))
                        myPoses.add(pos);
                    else
                        otherPoses.add(near);
                }
            });

            boolean change = true;
            while(change) {
                change = false;
                List<BlockPos> newPoses = new ArrayList<>();
                for(BlockPos pos : otherPoses) {
                    for(EnumFacing facing : poses.get(pos)) {
                        BlockPos near = pos.offset(facing);
                        if(myPoses.contains(near)) {
                            myPoses.add(pos);
                            change = true;
                        } else
                            newPoses.add(near);
                    }
                }
                otherPoses.clear();
                otherPoses.addAll(newPoses);
            }

            world.addScheduledTask(new UpdateNetworksTask(world, myPoses, SteamNetworkHandler.getSteamNetwork(world, first)));

            if(otherPoses.size() > 0) {
                Utils.getModForkJoinPool().execute(new ExtractNewNetworkFromBlocksTask(otherPoses.stream()
                        .map(pos -> Pair.of(pos, poses.get(pos)))
                        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)), world));
            }
        }
    }

    private static final class UpdateNetworksTask implements Runnable {
        private final World world;
        private final List<BlockPos> poses;
        private final SteamNetwork network;

        public UpdateNetworksTask(World world, List<BlockPos> poses, SteamNetwork network) {
            this.world = world;
            this.poses = poses;
            this.network = network;
        }

        @Override
        public void run() {
            boolean first = true;
            for(BlockPos pose : poses) {
                TileEntity tileEntity = world.getTileEntity(pose);
                if(tileEntity == null) {
                    ModLoggerHolder.getModLogger().log(Level.FATAL, "TileEntity not found!");
                    return;
                }

                SteamStorageImpl storage = null;
                if(tileEntity instanceof SteamStorageProvider) {
                    storage = (SteamStorageImpl) ((SteamStorageProvider) tileEntity).getSteamStorage();
                } else {
                    if(first) {
                        for(EnumFacing facing : EnumFacing.values()) {
                            if(tileEntity.hasCapability(SteamCapability.CAPABILITY, facing)) {
                                storage = (SteamStorageImpl) tileEntity.getCapability(SteamCapability.CAPABILITY, facing);
                                break;
                            }
                        }
                    } else {
                        for(EnumFacing facing : EnumFacing.values()) {
                            BlockPos near = pose.offset(facing);
                            if(poses.contains(near)) {
                                if(!tileEntity.hasCapability(SteamCapability.CAPABILITY, facing)) {
                                    ModLoggerHolder.getModLogger().log(Level.FATAL, "TileEntity has no capability!");
                                } else {
                                    storage = (SteamStorageImpl) tileEntity.getCapability(SteamCapability.CAPABILITY, facing);
                                    break;
                                }
                            }
                        }
                    }
                }

                if(storage == null)
                    continue;

                storage.setNetwork(network);
                first = false;
            }
        }
    }
}
