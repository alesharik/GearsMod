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
import net.minecraft.inventory.EntityEquipmentSlot;
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
            EntityEquipmentSlot slotIn = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
            ItemStack itemStack = playerIn.getItemStackFromSlot(slotIn);
            BasicSteamBoilerTileEntity tileEntity = (BasicSteamBoilerTileEntity) worldIn.getTileEntity(pos);
            if(tileEntity == null)
                return false;
            FluidStack contents = tileEntity.getWaterProperty().getContents();
            if(itemStack.getItem() == Items.WATER_BUCKET && contents != null && tileEntity.getWaterProperty().getCapacity() - contents.amount >= 1000) {
                itemStack.setCount(-1);
                playerIn.inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));
                tileEntity.addLiquid(1000);
            } else {
                IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if(capability == null)
                    return false;
                ItemStack stackInSlot = capability.getStackInSlot(0);
                if(TileEntityFurnace.isItemFuel(itemStack) && stackInSlot.getItem() == itemStack.getItem() && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                    int maxDelta = stackInSlot.getMaxStackSize() - stackInSlot.getCount();
                    int delta = maxDelta - itemStack.getCount();
                    if(delta < 0) {
                        itemStack.setCount(itemStack.getCount() + delta);
                        stackInSlot.setCount(stackInSlot.getMaxStackSize());
                    } else if(delta >= 0) {
                        playerIn.setItemStackToSlot(slotIn, ItemStack.EMPTY);
                        stackInSlot.setCount(stackInSlot.getCount() + itemStack.getCount());
                    }
                    return true;
                } else {
                    playerIn.openGui(GearsMod.getInstance(), ModGuis.BASIC_STEAM_BOILER.getGuiId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }
}
