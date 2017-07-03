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

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Consumer;

final class SteamStorageImpl implements SteamStorage {
    static final AtomicReferenceFieldUpdater<SteamStorageImpl, SteamNetwork> networkUpdater = AtomicReferenceFieldUpdater.newUpdater(SteamStorageImpl.class, SteamNetwork.class, "network");

    private final double maxForce;
    private final double volume;
    private final Consumer<Double> onOverload;

    private volatile SteamNetwork network;

    public SteamStorageImpl(double maxForce, double volume, Consumer<Double> onOverload, SteamNetwork network) {
        this.maxForce = maxForce;
        this.volume = volume;
        this.onOverload = onOverload;
        this.network = network;
    }

    @Nonnull
    @Override
    public SteamNetwork getNetwork() {
        return network;
    }

    @Override
    public double getMaxForce() {
        return maxForce;
    }

    @Override
    public void handleOverloadForce(double force) {
        onOverload.accept(force);
    }

    @Override
    public double getVolume() {
        return volume;
    }
}
