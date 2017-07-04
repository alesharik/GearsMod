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

package com.alesharik.gearsmod.tileEntity.steam;

import com.alesharik.gearsmod.capability.steam.SteamCapability;
import com.alesharik.gearsmod.capability.steam.SteamStorage;
import com.alesharik.gearsmod.steam.SteamNetworkHandler;
import com.alesharik.gearsmod.steam.SteamStorageProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public final class BasicSteamPipeTileEntity extends TileEntity implements SteamStorageProvider {
    private SteamStorage steamStorage;

    public BasicSteamPipeTileEntity() {
    }

    @Override
    public void onLoad() {
        steamStorage = SteamNetworkHandler.getStorageForBlock(world, pos, 800, 1200 * 1000 * 1000, aDouble -> {
            if(world instanceof WorldServer)
                ((WorldServer) world).addScheduledTask(() -> {
                    Explosion explosion = world.createExplosion(new EntityItem(world), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, true);
                    explosion.doExplosionA();
//                    explosion.doExplosionB(true);
                });
        });
        steamStorage.getNetwork().initBlock(pos);
    }

    public void onRemove() {
        steamStorage.getNetwork().destroyBlock(pos);
    }

    @Override
    public SteamStorage getSteamStorage() {
        return steamStorage;
    }

    @Nonnull
    @Override
    public EnumFacing[] getConnectedFacing() {
        return EnumFacing.values();
    }


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SteamCapability.CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == SteamCapability.CAPABILITY)
            return (T) steamStorage;
        return super.getCapability(capability, facing);
    }
}
