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

package com.alesharik.gearsmod.gui.container;

import com.alesharik.gearsmod.gui.container.slot.SlotFilteredItemHandler;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamBoilerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import javax.annotation.Nonnull;

public final class BasicSteamBoilerContainer extends TileEntityContainer<BasicSteamBoilerTileEntity> {

    public BasicSteamBoilerContainer(EntityPlayer player, BasicSteamBoilerTileEntity tileEntity) {
        super(tileEntity);

        for(int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(player.inventory, x, 57 + x * 17, 102));
        }

        this.addSlotToContainer(new SlotFilteredItemHandler(BurnableFilter.INSTANCE, tileEntity.getCoalItemStackHandler(), 0, 125, 79));

        addListener(ContainerSynchronizer.newInstance(player, new int[]{0}));
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        Slot firstSlot = this.inventorySlots.get(0);
        int playerInventorySize = 9;
        boolean playerInventoryFirst = firstSlot.inventory instanceof InventoryPlayer;

        if(slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(inventorySlots.size() == playerInventorySize) return ItemStack.EMPTY;
            if(playerInventoryFirst) {
                if(index < playerInventorySize) {
                    if(!this.mergeItemStack(itemstack1, playerInventorySize, this.inventorySlots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if(!this.mergeItemStack(itemstack1, 0, playerInventorySize, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if(index < this.inventorySlots.size() - playerInventorySize) {
                    if(!this.mergeItemStack(itemstack1, this.inventorySlots.size() - playerInventorySize, this.inventorySlots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if(!this.mergeItemStack(itemstack1, 0, this.inventorySlots.size() - playerInventorySize, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if(itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    private static final class BurnableFilter implements SlotFilteredItemHandler.Filter {
        static final BurnableFilter INSTANCE = new BurnableFilter();

        private BurnableFilter() {
        }

        @Override
        public boolean isValid(ItemStack stack) {
            return TileEntityFurnace.isItemFuel(stack);
        }
    }
}
