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

package com.alesharik.gearsmod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * This factory used by {@link GuiHandler} for create new GUI elements. It must be registered in {@link GuiHandler#registerFactory(GuiFactory)}
 *
 * @see GuiHandler
 */
public interface GuiFactory {
    /**
     * Return GUI id
     */
    int getGuiId();

    /**
     * Return client GUI element
     *
     * @param player   player, that interact wants to open GUI
     * @param world    world, where GUI must open
     * @param blockPos position of block, where GUI must open
     * @return client GUI element
     */
    @Nonnull
    Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos);

    /**
     * Return server GUI element
     *
     * @param player   player, that interact wants to open GUI
     * @param world    world, where GUI must open
     * @param blockPos position of block, where GUI must open
     * @return server GUI element
     */
    @Nonnull
    Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos);
}
