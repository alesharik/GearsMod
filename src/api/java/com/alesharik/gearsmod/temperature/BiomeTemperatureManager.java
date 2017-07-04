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

package com.alesharik.gearsmod.temperature;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BiomeTemperatureManager {
    private static final Map<Biome, TemperatureManager> biomes = new ConcurrentHashMap<>();
    private static final float maxHeightValue = 1.5F;

    private BiomeTemperatureManager() {
        throw new UnsupportedOperationException();
    }

    public static TemperatureManager getTemperatureManager(Biome biome, World world) {
        if(biomes.containsKey(biome))
            return biomes.get(biome);
        else {
            TemperatureManager temperatureManager = generateBiomeTemperatureManager(biome, world.getHeight());
            biomes.put(biome, temperatureManager);
            return temperatureManager;
        }
    }

    private static TemperatureManager generateBiomeTemperatureManager(Biome biome, int worldMaxHeight) {
        return new TemperatureManager((biome.getTemperature() * 100 - 32) / 1.8, getHeightFromBaseHeight(biome.getBaseHeight(), worldMaxHeight), biome.canRain());
    }

    private static int getHeightFromBaseHeight(float baseHeight, int worldMaxHeight) {
        return (int) (baseHeight / maxHeightValue * worldMaxHeight);
    }
}
