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
import com.alesharik.gearsmod.item.ModItems;
import com.alesharik.gearsmod.tileEntity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy {
    @SidedProxy
    private static CommonProxy proxy;

    public static CommonProxy getProxy() {
        return proxy;
    }

    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent event) {
        ModTileEntities.register();

        SmokeCapability.register();
    }

    public void onRegisterBlock(RegistryEvent.Register<Block> registry) {
        ModBlocks.register(registry.getRegistry());
    }

    public void onRegisterItem(RegistryEvent.Register<Item> register) {
        ModItems.register(register.getRegistry());
    }

    public static class ServerProxy extends CommonProxy {
    }

    public static class ClientProxy extends CommonProxy {
        @SideOnly(Side.CLIENT)
        @Override
        public void init(FMLInitializationEvent event) {
            super.init(event);

            ModItems.clientRegister();
            ModTileEntities.clientRegister();
        }

        @Override
        public void preInit(FMLPreInitializationEvent e) {
            super.preInit(e);

            OBJLoader.INSTANCE.addDomain(GearsMod.MODID);
        }
    }
}
