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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ContainerSynchronizer implements IContainerListener {
    private static final List<WeakReference<ContainerSynchronizer>> cache = new CopyOnWriteArrayList<>();
    private final EntityPlayer player;
    private final int[] updateSlots;

    private ContainerSynchronizer(EntityPlayer player, int[] updateSlots) {
        this.player = player;
        this.updateSlots = updateSlots;
    }

    public static ContainerSynchronizer newInstance(EntityPlayer player, int[] updateSlots) {
        ListIterator<WeakReference<ContainerSynchronizer>> iterator = cache.listIterator();
        while(iterator.hasNext()) {
            WeakReference<ContainerSynchronizer> next = iterator.next();
            if(next.isEnqueued()) {
                iterator.remove();
                continue;
            }
            ContainerSynchronizer synchronizer = next.get();
            if(synchronizer == null)
                continue;

            if(synchronizer.player.equals(player) && Arrays.equals(synchronizer.updateSlots, updateSlots))
                return synchronizer;
        }
        ContainerSynchronizer synchronizer = new ContainerSynchronizer(player, updateSlots);
        cache.add(new WeakReference<>(synchronizer));
        return synchronizer;
    }

    @Override
    public void updateCraftingInventory(@Nonnull Container containerToSend, @Nonnull NonNullList<ItemStack> itemsList) {

    }

    @Override
    public void sendSlotContents(@Nonnull Container containerToSend, int slotInd, @Nonnull ItemStack stack) {
        if(player instanceof EntityPlayerMP)
            ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetSlot(containerToSend.windowId, slotInd, stack));
    }

    @Override
    public void sendProgressBarUpdate(@Nonnull Container containerIn, int varToUpdate, int newValue) {

    }

    @Override
    public void sendAllWindowProperties(@Nonnull Container containerIn, @Nonnull IInventory inventory) {

    }
}
