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

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class provides all methods form {@link net.minecraft.client.gui.ScaledResolution} without creating new instance
 */
@SideOnly(Side.CLIENT)
public class ScaledScreenResolution {
    private static final Minecraft minecraftClient = Minecraft.getMinecraft();

    private ScaledScreenResolution() {
        throw new UnsupportedOperationException();
    }

    public static int getScaledWidth() {
        int scaledWidth = minecraftClient.displayWidth;
        int scaledHeight = minecraftClient.displayHeight;
        int scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if(i == 0) {
            i = 1000;
        }

        while(scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if(flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        scaledWidth = MathHelper.ceil((double) scaledWidth / (double) scaleFactor);

        return scaledWidth;
    }

    public static int getScaledHeight() {
        int scaledWidth = minecraftClient.displayWidth;
        int scaledHeight = minecraftClient.displayHeight;
        int scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if(i == 0) {
            i = 1000;
        }

        while(scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if(flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        scaledHeight = MathHelper.ceil((double) scaledHeight / (double) scaleFactor);

        return scaledHeight;
    }

    public static double getScaledDWidth() {
        int scaledWidth = minecraftClient.displayWidth;
        int scaledHeight = minecraftClient.displayHeight;
        int scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if(i == 0) {
            i = 1000;
        }

        while(scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if(flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        return (double) scaledWidth / (double) scaleFactor;
    }

    public static double getScaledDHeight() {
        int scaledWidth = minecraftClient.displayWidth;
        int scaledHeight = minecraftClient.displayHeight;
        int scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if(i == 0) {
            i = 1000;
        }

        while(scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if(flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        return (double) scaledHeight / (double) scaleFactor;
    }

    public static int getScaleFactor() {
        int scaledWidth = minecraftClient.displayWidth;
        int scaledHeight = minecraftClient.displayHeight;
        int scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if(i == 0) {
            i = 1000;
        }

        while(scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        if(flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }

        return scaleFactor;
    }
}
