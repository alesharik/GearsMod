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

package com.alesharik.gearsmod.tileEntity.temperature;

import com.alesharik.gearsmod.temperature.BiomeTemperatureManager;
import com.alesharik.gearsmod.util.provider.TemperatureProvider;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public final class ThermometerTileEntity extends TileEntity implements ITickable {
    private double temperature;
    private double maxTemperature;

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void update() {
        BlockPos targetPos = pos.offset(world.getBlockState(pos).getValue(BlockHorizontal.FACING));
        TileEntity targetTileEntity = world.getTileEntity(targetPos);
        if(targetTileEntity != null && targetTileEntity instanceof TemperatureProvider) {
            TemperatureProvider temperatureProvider = (TemperatureProvider) targetTileEntity;
            temperature = temperatureProvider.getTemperature();
            maxTemperature = temperatureProvider.getMaxTemperature();
        } else {
            maxTemperature = 100;
            temperature = BiomeTemperatureManager.getTemperatureManager(world.getBiome(pos), world).getTemperatureSmart(world, pos);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public EnumFacing getFacing() {
        return world.getBlockState(pos).getValue(BlockHorizontal.FACING);
    }
}
