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
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is implementation of {@link IGuiHandler}, which support GUI registration
 */
public final class GuiHandler implements IGuiHandler {
    private static final ConcurrentHashMap<Integer, GuiFactory> factories = new ConcurrentHashMap<>();
    private static final GuiHandler INSTANCE = new GuiHandler();

    private GuiHandler() {
    }

    /**
     * Register new GUI factory
     *
     * @param factory the factory
     */
    public static void registerFactory(@Nonnull GuiFactory factory) {
        if(factories.containsKey(factory.getGuiId()))
            throw new IllegalArgumentException("Factory already defined!");

        factories.put(factory.getGuiId(), factory);
    }

    /**
     * Return GuiHandler instance
     */
    public static GuiHandler getInstance() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(factories.containsKey(ID)) {
            return factories.get(ID).getServerGuiElement(player, world, new BlockPos.MutableBlockPos(x, y, z));
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(factories.containsKey(ID)) {
            return factories.get(ID).getClientGuiElement(player, world, new BlockPos.MutableBlockPos(x, y, z));
        } else {
            return null;
        }
    }
}
