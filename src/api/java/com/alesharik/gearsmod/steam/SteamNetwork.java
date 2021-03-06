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

import net.minecraft.util.math.BlockPos;

public interface SteamNetwork {
    /**
     * @param volume      in m3
     * @param temperature in Celsius
     */
    void addSteam(double volume, double temperature);

    boolean removeSteam(double volume);

    /**
     * @param t temperature in Celsius
     */
    void syncTemperature(double t);

    /**
     * @return steam temperature in Celsius
     */
    double getTemperature();

    double getPressure();

    /**
     * Call this after tileEntity loaded
     *
     * @param pos tileEntity position
     */
    void initBlock(BlockPos pos);

    /**
     * Call this before tileEntity delete
     *
     * @param pos tileEntity position
     */
    void destroyBlock(BlockPos pos);
}
