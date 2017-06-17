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

package com.alesharik.gearsmod.block;

import com.alesharik.gearsmod.block.decor.SmallBricksBlock;
import com.alesharik.gearsmod.block.energy.SimpleSolarPanelBlock;
import com.alesharik.gearsmod.block.smoke.ChimneyBlock;
import com.alesharik.gearsmod.block.smoke.SmokePipe;
import com.alesharik.gearsmod.block.steam.BasicSteamBoiler;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import static com.alesharik.gearsmod.GearsMod.getCreativeTab;

public final class ModBlocks {
    public static final BasicSteamBoiler BASIC_STEAM_BOILER = new BasicSteamBoiler();
    public static final SmokePipe SMOKE_PIPE = new SmokePipe();
    public static final ChimneyBlock CHIMNEY_BLOCK = new ChimneyBlock();
    public static final SmallBricksBlock SMALL_BRICKS_BLOCK = new SmallBricksBlock();
    public static final SimpleSolarPanelBlock SIMPLE_SOLAR_PANEL = new SimpleSolarPanelBlock();

    private ModBlocks() {
    }

    public static void register(IForgeRegistry<Block> registry) {
        registry.register(BASIC_STEAM_BOILER.setCreativeTab(getCreativeTab()));
        registry.register(SMOKE_PIPE.setCreativeTab(getCreativeTab()));
        registry.register(CHIMNEY_BLOCK.setCreativeTab(getCreativeTab()));
        registry.register(SMALL_BRICKS_BLOCK.setCreativeTab(getCreativeTab()));
        registry.register(SIMPLE_SOLAR_PANEL.setCreativeTab(getCreativeTab()));
    }
}
