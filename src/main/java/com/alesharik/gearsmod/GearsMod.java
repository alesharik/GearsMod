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

package com.alesharik.gearsmod;

import com.alesharik.gearsmod.block.ModBlocks;
import com.alesharik.gearsmod.proxy.CommonProxy;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.util.ModSecurityManager;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.alesharik.gearsmod.util.ModLoggerHolder.getModLogger;

@Mod(modid = GearsMod.MODID, version = GearsMod.VERSION)
public class GearsMod {
    public static final String MODID = "gearsmod";
    public static final String VERSION = "1.0-SNAPSHOT";

    private static final CreativeTabs CREATIVE_TAB = new CreativeTabs("Gears mod") {
        @SideOnly(Side.CLIENT)
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModBlocks.SMALL_BRICKS_BLOCK);
        }
    };

    private static SimpleNetworkWrapper networkWrapper;

    @Mod.Instance
    private static GearsMod instance;

    static {
        ModSecurityManager.setInstance(new ModSecurityManagerImpl());
    }

    public GearsMod() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Config.ConfigChangeListener(MODID));
    }

    public static CreativeTabs getCreativeTab() {
        return CREATIVE_TAB;
    }

    public static SimpleNetworkWrapper getNetworkWrapper() {
        return networkWrapper;
    }

    public static GearsMod getInstance() {
        return instance;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        networkWrapper = new SimpleNetworkWrapper(MODID);
        NetworkWrapperHolder.NETWORK_WRAPPER_ATOMIC_REFERENCE.set(networkWrapper);
        CurrentWorldProvider.provider.set(CommonProxy.getProxy());

        ModLoggerHolder.setModLogger(event.getModLog());

        Config.init(event.getSuggestedConfigurationFile());

        CommonProxy.getProxy().preInit(event);

        getModLogger().debug("PreInit stage completed!");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CommonProxy.getProxy().init(event);

        getModLogger().debug("Init stage completed!");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        CommonProxy.getProxy().postInit(event);

        getModLogger().debug("PostInit stage completed!");
    }

    @SubscribeEvent
    public void onRegisterBlock(RegistryEvent.Register<Block> event) {
        CommonProxy.getProxy().onRegisterBlock(event);
    }

    @SubscribeEvent
    public void onRegisterItem(RegistryEvent.Register<Item> event) {
        CommonProxy.getProxy().onRegisterItem(event);
    }
}
