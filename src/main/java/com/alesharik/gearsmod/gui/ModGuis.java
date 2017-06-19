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

import com.alesharik.gearsmod.gui.client.BasicSteamBoilerGui;
import com.alesharik.gearsmod.gui.server.BasicSteamBoilerContainer;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamBoilerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public enum ModGuis implements GuiFactory {
    BASIC_STEAM_BOILER {
        @Override
        public int getGuiId() {
            return 1;
        }

        @SideOnly(Side.CLIENT)
        @Nonnull
        @Override
        public Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
            return new BasicSteamBoilerGui(new BasicSteamBoilerContainer(player.inventory, (BasicSteamBoilerTileEntity) world.getTileEntity(blockPos)));
        }

        @Nonnull
        @Override
        public Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
            return new BasicSteamBoilerContainer(player.inventory, (BasicSteamBoilerTileEntity) world.getTileEntity(blockPos));
        }
    };

    static {
        for(ModGuis modGuis : ModGuis.values()) {
            GuiHandler.registerFactory(modGuis);
        }
    }
}
