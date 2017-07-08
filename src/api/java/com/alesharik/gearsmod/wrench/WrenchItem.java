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

package com.alesharik.gearsmod.wrench;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class WrenchItem extends Item {
    @Nonnull
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Wrench wrench = WrenchManager.newWrench(player.getHeldItem(hand));
        if(wrench == null)
            return EnumActionResult.FAIL;

        IBlockState blockState = worldIn.getBlockState(pos);
        Block block = blockState.getBlock();

        if(block instanceof WrenchHandler) {
            if(!((WrenchHandler) block).handleWrench(worldIn, pos, blockState, wrench, player, hitX, hitY, hitZ))
                return EnumActionResult.PASS;
        }

        if(player.isSneaking()) {
            if(block instanceof Wrenchable) {
                Wrenchable wrenchable = (Wrenchable) block;
                if(wrenchable.handleWrench(wrench)) {
                    wrenchable.wrenchBlock(worldIn, pos, blockState);
                    return EnumActionResult.SUCCESS;
                }
            } else if(block.getMaterial(blockState) instanceof Wrenchable) {
                Wrenchable material = (Wrenchable) block.getMaterial(blockState);
                if(material.handleWrench(wrench)) {
                    material.wrenchBlock(worldIn, pos, blockState);
                    return EnumActionResult.SUCCESS;
                }
            }
        } else {
            if(block instanceof Rotatable) {
                Rotatable rotatable = (Rotatable) block;
                if(rotatable.canRotate(worldIn, pos, blockState)) {
                    Rotator rotator = rotatable.getRotator();
                    EnumFacing next = rotator.getNextFacing(blockState);
                    rotator.rotateBlock(worldIn, pos, blockState, next);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public boolean canItemEditBlocks() {
        return true;
    }
}
