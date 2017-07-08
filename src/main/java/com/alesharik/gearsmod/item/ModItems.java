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

package com.alesharik.gearsmod.item;

import com.alesharik.gearsmod.block.ModBlocks;
import com.alesharik.gearsmod.wrench.WrenchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.alesharik.gearsmod.GearsMod.getCreativeTab;

public final class ModItems {
    public static ItemBlock BASIC_STEAM_BOILER_ITEM;
    public static ItemBlock SMOKE_PIPE_ITEM;
    public static ItemBlock CHIMNEY_BLOCK_ITEM;
    public static ItemBlock SMALL_BRICKS_BLOCK_ITEM;
    public static ItemBlock SIMPLE_SOLAR_PANEL_ITEM;
    public static ItemBlock BASIC_STEAM_PIPE_ITEM;
    public static ItemBlock THERMOMETER_BLOCK_ITEM;

    public static PortableThermometer PORTABLE_THERMOMETER;
    public static BasicItemWrench BASIC_WRENCH;

    private ModItems() {
    }

    public static void register(IForgeRegistry<Item> registry) {
        BASIC_STEAM_BOILER_ITEM = new ItemBlock(ModBlocks.BASIC_STEAM_BOILER);
        BASIC_STEAM_BOILER_ITEM.setUnlocalizedName("basic_steam_boiler");
        BASIC_STEAM_BOILER_ITEM.setRegistryName("basic_steam_boiler");
        BASIC_STEAM_BOILER_ITEM.setCreativeTab(getCreativeTab());
        registry.register(BASIC_STEAM_BOILER_ITEM);

        SMOKE_PIPE_ITEM = new ItemBlock(ModBlocks.SMOKE_PIPE);
        SMOKE_PIPE_ITEM.setRegistryName("smoke_pipe");
        SMOKE_PIPE_ITEM.setUnlocalizedName("smoke_pipe");
        SMOKE_PIPE_ITEM.setCreativeTab(getCreativeTab());
        registry.register(SMOKE_PIPE_ITEM);

        CHIMNEY_BLOCK_ITEM = new ItemBlock(ModBlocks.CHIMNEY_BLOCK);
        CHIMNEY_BLOCK_ITEM.setUnlocalizedName("chimney_block");
        CHIMNEY_BLOCK_ITEM.setRegistryName("chimney_block");
        CHIMNEY_BLOCK_ITEM.setCreativeTab(getCreativeTab());
        registry.register(CHIMNEY_BLOCK_ITEM);

        SMALL_BRICKS_BLOCK_ITEM = new ItemBlock(ModBlocks.SMALL_BRICKS_BLOCK);
        SMALL_BRICKS_BLOCK_ITEM.setUnlocalizedName("small_bricks_block");
        SMALL_BRICKS_BLOCK_ITEM.setRegistryName("small_bricks_block");
        SMALL_BRICKS_BLOCK_ITEM.setCreativeTab(getCreativeTab());
        registry.register(SMALL_BRICKS_BLOCK_ITEM);

        SIMPLE_SOLAR_PANEL_ITEM = new ItemBlock(ModBlocks.SIMPLE_SOLAR_PANEL);
        SIMPLE_SOLAR_PANEL_ITEM.setUnlocalizedName("simple_solar_panel");
        SIMPLE_SOLAR_PANEL_ITEM.setRegistryName("simple_solar_panel");
        SIMPLE_SOLAR_PANEL_ITEM.setCreativeTab(getCreativeTab());
        registry.register(SIMPLE_SOLAR_PANEL_ITEM);

        BASIC_STEAM_PIPE_ITEM = new ItemBlock(ModBlocks.BASIC_STEAM_PIPE);
        BASIC_STEAM_PIPE_ITEM.setUnlocalizedName("basic_steam_pipe");
        BASIC_STEAM_PIPE_ITEM.setRegistryName("basic_steam_pipe");
        BASIC_STEAM_PIPE_ITEM.setCreativeTab(getCreativeTab());
        registry.register(BASIC_STEAM_PIPE_ITEM);

        THERMOMETER_BLOCK_ITEM = new ItemBlock(ModBlocks.THERMOMETER_BLOCK);
        THERMOMETER_BLOCK_ITEM.setUnlocalizedName("thermometer_block");
        THERMOMETER_BLOCK_ITEM.setRegistryName("thermometer_block");
        THERMOMETER_BLOCK_ITEM.setCreativeTab(getCreativeTab());
        registry.register(THERMOMETER_BLOCK_ITEM);

        PORTABLE_THERMOMETER = new PortableThermometer();
        registry.register(PORTABLE_THERMOMETER);

        BASIC_WRENCH = new BasicItemWrench();
        WrenchManager.registerFactory(new BasicItemWrench.WrenchWrapperFactory());
        registry.register(BASIC_WRENCH);
    }

    @SideOnly(Side.CLIENT)
    public static void clientRegister() {
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        modelMesher.register(BASIC_STEAM_BOILER_ITEM, 0, new ModelResourceLocation("gearsmod:basic_steam_boiler", "inventory"));
        modelMesher.register(SMOKE_PIPE_ITEM, 0, new ModelResourceLocation("gearsmod:smoke_pipe", "inventory"));
        modelMesher.register(CHIMNEY_BLOCK_ITEM, 0, new ModelResourceLocation("gearsmod:chimney_block", "inventory"));
        modelMesher.register(SMALL_BRICKS_BLOCK_ITEM, 0, new ModelResourceLocation("gearsmod:small_bricks_block", "inventory"));
        modelMesher.register(SIMPLE_SOLAR_PANEL_ITEM, 0, new ModelResourceLocation("gearsmod:simple_solar_panel", "inventory"));

        modelMesher.register(BASIC_STEAM_PIPE_ITEM, 0, new ModelResourceLocation("gearsmod:basic_steam_pipe", "inventory"));

        modelMesher.register(PORTABLE_THERMOMETER, 0, new ModelResourceLocation("gearsmod:portable_thermometer", "inventory"));
        modelMesher.register(PORTABLE_THERMOMETER, 1, new ModelResourceLocation("gearsmod:portable_thermometer", "inventory"));
        modelMesher.register(PORTABLE_THERMOMETER, 2, new ModelResourceLocation("gearsmod:portable_thermometer", "inventory"));

        modelMesher.register(BASIC_WRENCH, 0, new ModelResourceLocation("gearsmod:basic_wrench", "inventory"));
    }
}
