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

package com.alesharik.gearsmod.tileEntity;

import com.alesharik.gearsmod.tileEntity.energy.SimpleSolarPanelTileEntity;
import com.alesharik.gearsmod.tileEntity.smoke.ChimneyTileEntity;
import com.alesharik.gearsmod.tileEntity.smoke.SmokePipeTileEntity;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamBoilerTileEntity;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamPipeTileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModTileEntities {
    public static final String BASIC_STEAM_BOILER_TILE_ENTITY_ID = "GearsModBasicSteamBoilerTileEntity";
    public static final String SMOKE_PIPE_TILE_ENTITY_ID = "GearsSmokePipeTileEntity";
    public static final String CHIMNEY_TILE_ENTITY = "GearsChimneyTileEntity";
    public static final String SIMPLE_SOLAR_PANEL_TILE_ENTITY = "GearsSimpleSolarPanelTileEntity";
    public static final String BASIC_STEAM_PIPE_TILE_ENTITY = "GearsBasicSteamPipeTileEntity";

    private ModTileEntities() {
    }

    public static void register() {
        GameRegistry.registerTileEntity(BasicSteamBoilerTileEntity.class, BASIC_STEAM_BOILER_TILE_ENTITY_ID);
        GameRegistry.registerTileEntity(SmokePipeTileEntity.class, SMOKE_PIPE_TILE_ENTITY_ID);
        GameRegistry.registerTileEntity(ChimneyTileEntity.class, CHIMNEY_TILE_ENTITY);
        GameRegistry.registerTileEntity(SimpleSolarPanelTileEntity.class, SIMPLE_SOLAR_PANEL_TILE_ENTITY);
        GameRegistry.registerTileEntity(BasicSteamPipeTileEntity.class, BASIC_STEAM_PIPE_TILE_ENTITY);
    }

    @SideOnly(Side.CLIENT)
    public static void clientRegister() {
//        ClientRegistry.bindTileEntitySpecialRenderer(ChimneyTileEntity.class, new ChimneyTileEntityRenderer());
    }
}
