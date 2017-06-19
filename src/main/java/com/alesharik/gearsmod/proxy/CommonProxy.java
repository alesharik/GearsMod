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

import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.block.ModBlocks;
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.gui.GuiHandler;
import com.alesharik.gearsmod.integration.IntegrationManager;
import com.alesharik.gearsmod.item.ModItems;
import com.alesharik.gearsmod.tileEntity.ModTileEntities;
import com.alesharik.gearsmod.util.ExecutionUtils;
import com.alesharik.gearsmod.util.field.SimpleTileEntityFieldStore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy implements ExecutionUtils.Executor {
    @SidedProxy
    private static CommonProxy proxy;

    public static CommonProxy getProxy() {
        return proxy;
    }

    public void preInit(FMLPreInitializationEvent e) {
        ExecutionUtils.initialize(proxy);
        IntegrationManager.preInit();
    }

    public void init(FMLInitializationEvent event) {
        ModTileEntities.register();

        SmokeCapability.register();

        NetworkRegistry.INSTANCE.registerGuiHandler(GearsMod.getInstance(), GuiHandler.getInstance());

        GearsMod.getNetworkWrapper().registerMessage(SimpleTileEntityFieldStore.FieldUpdateMessage.Handler.class, SimpleTileEntityFieldStore.FieldUpdateMessage.class, 0, Side.SERVER);
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

    public static class ServerProxy extends CommonProxy {
    }

    public static class ClientProxy extends CommonProxy {
        @SideOnly(Side.CLIENT)
        @Override
        public void init(FMLInitializationEvent event) {
            super.init(event);

            GearsMod.getNetworkWrapper().registerMessage(SimpleTileEntityFieldStore.FieldUpdateMessage.Handler.class, SimpleTileEntityFieldStore.FieldUpdateMessage.class, 0, Side.CLIENT);

            ModItems.clientRegister();
            ModTileEntities.clientRegister();
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
    }
}
