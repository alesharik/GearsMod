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

package com.alesharik.gearsmod.block.steam;

import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.block.BlockMachine;
import com.alesharik.gearsmod.gui.ModGuis;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamBoilerTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public final class BasicSteamBoiler extends BlockMachine {
    public BasicSteamBoiler() {
        super(Material.IRON);
        this.setUnlocalizedName("basic_steam_boiler");
        this.setRegistryName("basic_steam_boiler");
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new BasicSteamBoilerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            BasicSteamBoilerTileEntity tileEntity = (BasicSteamBoilerTileEntity) worldIn.getTileEntity(pos);
            if(tileEntity == null)
                return false;

            ItemStack playerHeldItem = playerIn.getHeldItem(hand);
            FluidStack waterTankContents = tileEntity.getWaterProperty().getContents();
            if(tryHandleAsBucket(playerIn, tileEntity, playerHeldItem, waterTankContents)) return true;

            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if(capability == null)
                return false;

            ItemStack burnableStack = capability.getStackInSlot(0);
            if(tryHandleAsBurnable(playerIn, hand, playerHeldItem, burnableStack, capability)) return true;

            playerIn.openGui(GearsMod.getInstance(), ModGuis.BASIC_STEAM_BOILER.getGuiId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    private boolean tryHandleAsBurnable(EntityPlayer playerIn, EnumHand hand, ItemStack playerHeldItem, ItemStack burnableStack, IItemHandler handler) {
        if(!TileEntityFurnace.isItemFuel(playerHeldItem))
            return false;

        if(TileEntityFurnace.isItemFuel(playerHeldItem) && burnableStack.getItem() == playerHeldItem.getItem() && burnableStack.getCount() < burnableStack.getMaxStackSize()) {
            int maxDelta = burnableStack.getMaxStackSize() - burnableStack.getCount();
            int delta = maxDelta - playerHeldItem.getCount();
            if(delta < 0) {
                playerHeldItem.setCount(playerHeldItem.getCount() + delta);
                burnableStack.setCount(burnableStack.getMaxStackSize());
            } else if(delta >= 0) {
                playerIn.setHeldItem(hand, ItemStack.EMPTY);
                burnableStack.setCount(burnableStack.getCount() + playerHeldItem.getCount());
            }
            return true;
        } else if(burnableStack.getCount() <= 0) {
            playerIn.setHeldItem(hand, ItemStack.EMPTY);
            handler.insertItem(0, playerHeldItem, false);
        }
        return false;
    }

    private boolean tryHandleAsBucket(EntityPlayer playerIn, BasicSteamBoilerTileEntity tileEntity, ItemStack playerHeldItem, FluidStack waterTankContents) {
        if(playerHeldItem.getItem() == Items.WATER_BUCKET && waterTankContents != null && tileEntity.getWaterProperty().getCapacity() - waterTankContents.amount >= 1000) {
            playerHeldItem.setCount(playerHeldItem.getCount() - 1);//Remove 1 water bucket
            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));//Add 1 empty bucket
            tileEntity.addLiquid(1000);//Add 1b water
            return true;
        }
        return false;
    }
}
