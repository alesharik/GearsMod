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

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class TemperatureManager {
    private static final float temeratureChangePerOneHundredBlocks = 0.65F;

    private final double defaultTemperature;
    private final int defaultHeight;
    private final boolean canRain;

    TemperatureManager(double defaultTemperature, int defaultHeight, boolean hasRain) {
        this.defaultTemperature = defaultTemperature;
        this.defaultHeight = defaultHeight;
        this.canRain = hasRain;
    }

    /**
     * @return temperature in Celsius
     */
    public double getBaseTemperature() {
        return defaultTemperature;
    }

    /**
     * @return temperature in Celsius
     */
    public double getTemperature(int height) {
        int dHeight = height - defaultHeight;
        return defaultTemperature + (dHeight / 100 * temeratureChangePerOneHundredBlocks);
    }

    public double getTemperatureSmart(World world, BlockPos pos) {
        double temp = getTemperature(pos.getY());

        if(canRain && world.isRaining() && world.canBlockSeeSky(pos.offset(EnumFacing.UP))) {
            if(defaultTemperature > 0)
                temp -= 3;
            else
                temp -= 8;
        }

        BlockPos iteratorPos = pos.offset(EnumFacing.UP);
        while(world.getBlockState(iteratorPos).getBlock() == Blocks.WATER || world.getBlockState(iteratorPos).getBlock() == Blocks.FLOWING_WATER) {
            iteratorPos = iteratorPos.offset(EnumFacing.UP);
            temp -= 1;
        }

        return temp;
    }
}
