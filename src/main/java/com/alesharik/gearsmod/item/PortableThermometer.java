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
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public final class PortableThermometer extends Item {
    public PortableThermometer() {
        setUnlocalizedName("portable_thermometer");
        setRegistryName("portable_thermometer");
        setCreativeTab(GearsMod.getCreativeTab());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("description.show.mode") + ": " + getMode(stack).getNameLocalized());//Mode: NONE
        tooltip.add(I18n.format("description.thermometer.measure.range")); //Measures from -33° C to 100° C
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);

        if(!playerIn.isSneaking())
            return new ActionResult<>(EnumActionResult.FAIL, item);

        Mode mode = getMode(item).cycle();
        item.setItemDamage(mode.getId());

        return new ActionResult<>(EnumActionResult.PASS, item);
    }

    public Mode getMode(ItemStack stack) {
        return Mode.getMode(stack.getItemDamage());
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return stack.getItemDamage();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    public enum Mode {
        NONE(0),
        HOLD(1),
        ALWAYS(2);

        private final int id;

        Mode(int id) {
            this.id = id;
        }

        public static Mode getMode(int id) {
            if(id == 0)
                return Mode.NONE;
            else if(id == 1)
                return Mode.HOLD;
            else if(id == 2)
                return Mode.ALWAYS;

            return Mode.NONE;
        }

        public int getId() {
            return id;
        }

        @SideOnly(Side.CLIENT)
        public String getNameLocalized() {
            if(this == Mode.NONE)
                return I18n.format("description.thermometer.mode.none");
            if(this == Mode.HOLD)
                return I18n.format("description.thermometer.mode.hold");
            if(this == Mode.ALWAYS)
                return I18n.format("description.thermometer.mode.always");
            return "";
        }

        public Mode cycle() {
            if(this == Mode.NONE)
                return Mode.HOLD;
            else if(this == Mode.HOLD)
                return Mode.ALWAYS;
            else if(this == Mode.ALWAYS)
                return Mode.NONE;

            return Mode.NONE;
        }
    }
}
