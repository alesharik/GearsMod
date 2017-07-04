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

import com.alesharik.gearsmod.capability.steam.SteamStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SteamNetworkHandler {
    private static final Map<Integer, WorldSteamNetworks> networks = new ConcurrentHashMap<>();

    private SteamNetworkHandler() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param world
     * @param pos
     * @param volume
     * @param maxForce
     * @param onOverload will be executed in another thread!
     * @return
     */
    public static SteamStorage getStorageForBlock(@Nonnull World world, @Nonnull BlockPos pos, double volume, double maxForce, @Nonnull Consumer<Double> onOverload) {
        int id = world.provider.getDimension();
        WorldSteamNetworks network;
        if(networks.containsKey(id)) {
            network = networks.get(id);
        } else {
            network = WorldSteamNetworks.getNetworks(world);
            networks.put(id, network);
        }

        BasicSteamNetwork network1 = network.getNetwork(world, pos);
        SteamStorageImpl storage = new SteamStorageImpl(volume, maxForce, onOverload, network1);
        network1.bakeSteamStorage(storage);
        return storage;
    }

    static SteamNetwork getSteamNetwork(World world, BlockPos pos) {
        int id = world.provider.getDimension();
        WorldSteamNetworks network;
        if(networks.containsKey(id)) {
            network = networks.get(id);
        } else {
            network = WorldSteamNetworks.getNetworks(world);
            networks.put(id, network);
        }

        return network.getNetwork(world, pos);
    }

    static void markDirty(BasicSteamNetwork network) {
        for(WorldSteamNetworks worldSteamNetworks : networks.values()) {
            if(worldSteamNetworks.markDirty(network))
                return;
        }
    }

    static void deleteNetwork(BasicSteamNetwork network) {
        for(WorldSteamNetworks worldSteamNetworks : networks.values()) {
            if(worldSteamNetworks.deleteNetwork(network))
                return;
        }
    }
}
