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

import com.alesharik.gearsmod.util.Utils;
import com.alesharik.gearsmod.util.world.ConcurrentWorldSavedData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public final class WorldSteamNetworks extends ConcurrentWorldSavedData {
    private static final String NAME = "gearsmod:steam";
    private final List<BasicSteamNetwork> networks = new CopyOnWriteArrayList<>();
    private World world;

    public WorldSteamNetworks() {
        super(NAME);
    }

    @SuppressWarnings("unused") //Forge use it for create instance
    public WorldSteamNetworks(String name) {
        super(name);
        if(!NAME.equals(name))
            throw new IllegalArgumentException("DataIdentifier " + name + " must be " + NAME);
    }

    @Nonnull
    public static WorldSteamNetworks getNetworks(@Nonnull World world) {
        MapStorage storage = world.getPerWorldStorage();
        WorldSteamNetworks instance = (WorldSteamNetworks) storage.getOrLoadData(WorldSteamNetworks.class, NAME);
        if(instance == null) {
            instance = new WorldSteamNetworks();
            storage.setData(NAME, instance);

            instance.markDirty();
            storage.saveAllData();
        } else {
            Utils.getModForkJoinPool().execute(new CleanNetworksTask(instance));
        }
        return instance;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("networks", Constants.NBT.TAG_COMPOUND);
        IntStream.range(0, list.tagCount())
                .mapToObj(list::get)
                .map(nbtBase -> (NBTTagCompound) nbtBase)
                .map(nbtBase -> {
                    BasicSteamNetwork network = new BasicSteamNetwork(world);
                    network.deserializeNBT(nbt);
                    return network;
                })
                .forEach(networks::add);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        networks.stream()
                .map(BasicSteamNetwork::serializeNBT)
                .forEach(list::appendTag);
        compound.setTag("networks", list);
        return compound;
    }

    BasicSteamNetwork getNetwork(World world, BlockPos pos) {
        for(BasicSteamNetwork network : networks) {
            if(network.validBlockPos(world, pos))
                return network;
        }
        BasicSteamNetwork steamNetwork = new BasicSteamNetwork(world);
        networks.add(steamNetwork);
        return steamNetwork;
    }

    boolean markDirty(BasicSteamNetwork network) {
        for(BasicSteamNetwork steamNetwork : networks) {
            if(steamNetwork.equals(network)) {
                markDirty();
                return true;
            }
        }
        return false;
    }

    boolean deleteNetwork(BasicSteamNetwork network) {
        return networks.remove(network);
    }

    /**
     * This task check all networks for 0 poses
     */
    private static final class CleanNetworksTask implements Runnable {
        private final WorldSteamNetworks networks;

        CleanNetworksTask(WorldSteamNetworks networks) {
            this.networks = networks;
        }

        @Override
        public void run() {
            boolean change = false;
            List<BasicSteamNetwork> networks = this.networks.networks;
            for(BasicSteamNetwork network : networks) {
                if(network.getPoseCount() == 0) {
                    networks.remove(network);
                    change = true;
                }
            }
            if(change)
                this.networks.markDirty();
        }
    }
}
