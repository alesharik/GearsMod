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

package com.alesharik.gearsmod.proxy;

import com.alesharik.gearsmod.CurrentWorldProvider;
import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.block.ModBlocks;
import com.alesharik.gearsmod.capability.fluid.FluidSynchronizationMessage;
import com.alesharik.gearsmod.capability.fluid.RequestFluidSynchronizationMessage;
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeChangeMessage;
import com.alesharik.gearsmod.capability.steam.SteamCapability;
import com.alesharik.gearsmod.gui.GuiHandler;
import com.alesharik.gearsmod.integration.IntegrationManager;
import com.alesharik.gearsmod.integration.theoneprobe.TheOneProbeIntegrationModule;
import com.alesharik.gearsmod.item.ItemOverlaysRenderer;
import com.alesharik.gearsmod.item.ModItems;
import com.alesharik.gearsmod.tileEntity.ModTileEntities;
import com.alesharik.gearsmod.util.ExecutionUtils;
import com.alesharik.gearsmod.util.field.SimpleTileEntityFieldStore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy implements ExecutionUtils.Executor, CurrentWorldProvider.Provider {
    @SidedProxy
    private static CommonProxy proxy;

    public static CommonProxy getProxy() {
        return proxy;
    }

    public void preInit(FMLPreInitializationEvent e) {
        IntegrationManager.addIntegrationModule(new TheOneProbeIntegrationModule());

        ExecutionUtils.initialize(proxy);
        IntegrationManager.preInit();
    }

    public void init(FMLInitializationEvent event) {
        ModTileEntities.register();

        SmokeCapability.register();
        SteamCapability.register();

        NetworkRegistry.INSTANCE.registerGuiHandler(GearsMod.getInstance(), GuiHandler.getInstance());

        SimpleNetworkWrapper networkWrapper = GearsMod.getNetworkWrapper();
        networkWrapper.registerMessage(SimpleTileEntityFieldStore.FieldUpdateMessage.Handler.class, SimpleTileEntityFieldStore.FieldUpdateMessage.class, 0, Side.SERVER);
        networkWrapper.registerMessage(SmokeChangeMessage.Handler.class, SmokeChangeMessage.class, 1, Side.SERVER);
        networkWrapper.registerMessage(FluidSynchronizationMessage.Handler.class, FluidSynchronizationMessage.class, 2, Side.SERVER);
        networkWrapper.registerMessage(RequestFluidSynchronizationMessage.Handler.class, RequestFluidSynchronizationMessage.class, 3, Side.SERVER);
        IntegrationManager.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        IntegrationManager.postInit();
    }

    public void onRegisterBlock(RegistryEvent.Register<Block> registry) {
        ModBlocks.register(registry.getRegistry());
    }

    public void onRegisterItem(RegistryEvent.Register<Item> register) {
        ModItems.register(register.getRegistry());
    }

    @Override
    public void executeTask(MessageContext context, Runnable runnable) {
        if(context.getServerHandler().playerEntity.world instanceof WorldServer) {
            ((WorldServer) context.getServerHandler().playerEntity.world).addScheduledTask(runnable);
        }
    }

    @Override
    public World getWorld(MessageContext context, Side side) {
        if(side == Side.SERVER && context.getServerHandler().playerEntity.world instanceof WorldServer) {
            return context.getServerHandler().playerEntity.getServerWorld();
        }
        return null;
    }

    public static class ServerProxy extends CommonProxy {
    }

    public static class ClientProxy extends CommonProxy {
        @SideOnly(Side.CLIENT)
        @Override
        public void init(FMLInitializationEvent event) {
            super.init(event);

            SimpleNetworkWrapper networkWrapper = GearsMod.getNetworkWrapper();
            networkWrapper.registerMessage(SimpleTileEntityFieldStore.FieldUpdateMessage.Handler.class, SimpleTileEntityFieldStore.FieldUpdateMessage.class, 0, Side.CLIENT);
            networkWrapper.registerMessage(SmokeChangeMessage.Handler.class, SmokeChangeMessage.class, 1, Side.CLIENT);
            networkWrapper.registerMessage(FluidSynchronizationMessage.Handler.class, FluidSynchronizationMessage.class, 2, Side.CLIENT);
            networkWrapper.registerMessage(RequestFluidSynchronizationMessage.Handler.class, RequestFluidSynchronizationMessage.class, 3, Side.CLIENT);

            ModItems.clientRegister();
            ModTileEntities.clientRegister();

            MinecraftForge.EVENT_BUS.register(new ItemOverlaysRenderer());
        }

        @Override
        public void preInit(FMLPreInitializationEvent e) {
            super.preInit(e);

            OBJLoader.INSTANCE.addDomain(GearsMod.MODID);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void executeTask(MessageContext context, Runnable runnable) {
            if(context.side == Side.CLIENT)
                Minecraft.getMinecraft().addScheduledTask(runnable);
            else
                super.executeTask(context, runnable);
        }

        @SideOnly(Side.CLIENT)
        @Override
        public World getWorld(MessageContext context, Side side) {
            if(side == Side.CLIENT) {
                return Minecraft.getMinecraft().world;
            } else {
                return super.getWorld(context, side);
            }
        }
    }
}
