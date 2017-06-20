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

package com.alesharik.gearsmod.capability.smoke;

import com.alesharik.gearsmod.NetworkWrapperHolder;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SmokeHandlerSynchronizer {
    private SmokeHandlerSynchronizer() {
        throw new UnsupportedOperationException();
    }

    /**
     * This will work if tileEntity has SmokeCapability. Try not use on client
     *
     * @param world    the world, where tileEntity located
     * @param blockPos position of tileEntity
     * @param facing   where smokeCapability located
     */
    public static void synchronize(@Nonnull World world, @Nonnull BlockPos blockPos, @Nullable EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if(tileEntity == null || !tileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, facing))
            return;

        SmokeStorage smokeStorage = tileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, facing);
        if(smokeStorage == null)
            return;

        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            NetworkWrapperHolder.getNetworkWrapper().sendToDimension(new SmokeChangeMessage(smokeStorage.getSmokeAmount(), blockPos, world, facing), world.provider.getDimension());
        } else {
            ModLoggerHolder.getModLogger().info("Detected sync message about smoke from client to server!");
            NetworkWrapperHolder.getNetworkWrapper().sendToServer(new SmokeChangeMessage(smokeStorage.getSmokeAmount(), blockPos, world, facing));
        }
    }
}
