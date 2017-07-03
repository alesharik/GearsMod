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

package com.alesharik.gearsmod.util;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

@SuppressWarnings("WeakerAccess")
public final class PhysicMath {
    public static final double WATER_BUCKETS_FOR_COAL = 10;
    public static final double MEGA_JOULES_PER_WATER_BUCKET = 2.256;
    public static final double MEGA_JOULES_PER_MILLI_BUCKET = MEGA_JOULES_PER_WATER_BUCKET / 1000;
    public static final int COAL_BURN_TIME = TileEntityFurnace.getItemBurnTime(new ItemStack(Items.COAL));
    public static final double COAL_MEGA_JOULES = MEGA_JOULES_PER_WATER_BUCKET * WATER_BUCKETS_FOR_COAL;
    private static final double ONE_MEGA_PASCAL = Math.pow(10, 6);
    private static final double N1_FOR_STEAM_CURVE = 1167.0521452767;
    private static final double N2_FOR_STEAM_CURVE = -724213.16703206;
    private static final double N3_FOR_STEAM_CURVE = -17.073846940092;
    private static final double N4_FOR_STEAM_CURVE = 12020.82470247;
    private static final double N5_FOR_STEAM_CURVE = -3232555.0322333;
    private static final double N6_FOR_STEAM_CURVE = 14.91510861353;
    private static final double N7_FOR_STEAM_CURVE = -4823.2657361591;
    private static final double N8_FOR_STEAM_CURVE = 405113.40542057;
    private static final double N9_FOR_STEAM_CURVE = -0.23855557567849;
    private static final double N10_FOR_STEAM_CURVE = 650.17534844798;
    private static final double TEMPERATURE_ZERO = 1;//1K
    private static final double PRESSURE_ZERO = 1;//1MPa

    private PhysicMath() {
        throw new UnsupportedOperationException();
    }

    public static double burnableItemToMegaJoules(ItemStack stack) {
        int burnTime = TileEntityFurnace.getItemBurnTime(stack);
        double relationForCoal = burnTime * 1F / COAL_BURN_TIME;
        return relationForCoal * COAL_MEGA_JOULES;
    }

    public static double celsiusToKelvin(double celsius) {
        return celsius + 273.15;
    }

    /**
     * This is for 1 m3 of steam
     *
     * @param kelvin temperature in Kelvin
     * @return pressure in MPa(mega Pascal)
     */
    public static double getSteamPressureForTemperature(double kelvin) {
        double kelvinDividedByKelvinZero = kelvin / TEMPERATURE_ZERO;
        double theta = kelvinDividedByKelvinZero + (N9_FOR_STEAM_CURVE / (kelvinDividedByKelvinZero - N10_FOR_STEAM_CURVE));
        double powOfTheta = Math.pow(theta, 2);
        double a = powOfTheta + theta * N1_FOR_STEAM_CURVE + N2_FOR_STEAM_CURVE;
        double b = N3_FOR_STEAM_CURVE * powOfTheta + N4_FOR_STEAM_CURVE * theta + N5_FOR_STEAM_CURVE;
        double c = N6_FOR_STEAM_CURVE * powOfTheta + N7_FOR_STEAM_CURVE * theta + N8_FOR_STEAM_CURVE;

        double beta = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a);
        return PRESSURE_ZERO * Math.pow(beta, 4) / ONE_MEGA_PASCAL;
    }

    /**
     * @param pressure measure in MPa(mega Pascal)
     * @param volume   measure in m3
     * @return force in MN(mega Newton)
     */
    public static double getForceByPressureAndVolume(double pressure, double volume) {
        return pressure / volume;
    }

    public static double getMegaJoulesWithEfficiency(double base, double count, double max) {
        double v = base / max * (max - count);
        if(v < 0)
            return 0;
        return v;
    }
}
