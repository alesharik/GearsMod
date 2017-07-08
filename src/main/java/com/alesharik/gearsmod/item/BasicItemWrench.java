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

import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.wrench.Wrench;
import com.alesharik.gearsmod.wrench.WrenchItem;
import com.alesharik.gearsmod.wrench.WrenchManager;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class BasicItemWrench extends WrenchItem {
    public BasicItemWrench() {
        setCreativeTab(GearsMod.getCreativeTab());
        setRegistryName("basic_wrench");
        setUnlocalizedName("basic_wrench");
        setMaxStackSize(1);
        setMaxDamage(500);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    static final class WrenchWrapperFactory implements WrenchManager.Factory {

        @Override
        public boolean accept(ItemStack stack) {
            return stack.getItem() == ModItems.BASIC_WRENCH; //TODO use instance
        }

        @Nonnull
        @Override
        public Wrench newWrench(ItemStack stack) {
            return new WrenchWrapper(stack);
        }

        private static final class WrenchWrapper implements Wrench {
            private final ItemStack stack;

            WrenchWrapper(ItemStack stack) {
                this.stack = stack;
            }

            @Override
            public void damageWrench(int delta) {
                stack.setItemDamage(stack.getItemDamage() - delta);
            }

            @Override
            public int getWrenchDamage() {
                return stack.getItemDamage();
            }

            @Override
            public ItemStack getItemStack() {
                return stack;
            }

            @Override
            public Tier getTier() {
                return Tier.BASIC;
            }
        }
    }
}
