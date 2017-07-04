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

package com.alesharik.gearsmod.tileEntity.smoke;

import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeHandler;
import com.alesharik.gearsmod.capability.smoke.SmokeHandlerSynchronizer;
import com.alesharik.gearsmod.capability.smoke.SmokeStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SmokePipeTileEntity extends TileEntity implements ITickable {
    private static final EnumFacing[] SIDES = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.SOUTH};

    private final SmokeHandler smokeHandler;

    public SmokePipeTileEntity() {
        smokeHandler = new SmokeHandler(500, true, true);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SmokeCapability.DEFAULT_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return (capability == SmokeCapability.DEFAULT_CAPABILITY) ? (T) smokeHandler : super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            boolean topInsert = false;
            //Sync with sides
            for(EnumFacing side : SIDES) {
                TileEntity tileEntity = world.getTileEntity(pos.offset(side));
                if(tileEntity != null && tileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, side.getOpposite())) {
                    SmokeStorage smokeStorage = tileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, side.getOpposite());
                    if(smokeStorage != null) {
                        if(smokeStorage.canReceive() && smokeHandler.getSmokeAmount() > smokeStorage.getSmokeAmount() && !smokeStorage.overloaded()
                                && smokeStorage.getSmokeAmount() + 20 < smokeStorage.getMaxSmokeAmount()) {
                            int delete = smokeHandler.extract(10, false);
                            smokeStorage.receive(delete);
                        } else if(smokeStorage.canExtract() && smokeHandler.getSmokeAmount() < smokeStorage.getSmokeAmount() && !smokeHandler.overloaded()
                                && smokeHandler.getSmokeAmount() + 20 < smokeHandler.getMaxSmokeAmount()) {
                            int delete = smokeStorage.extract(10, false);
                            smokeHandler.receive(delete);
                        } else if(smokeHandler.overloaded()) {
                            int delete = smokeHandler.extract(10, false);
                            smokeStorage.receive(delete);
                        }
                        SmokeHandlerSynchronizer.synchronize(world, pos.offset(side), side.getOpposite());
                    }
                }
            }

            //Try insert into top tileEntity
            TileEntity topTileEntity = world.getTileEntity(pos.offset(EnumFacing.UP));
            if(topTileEntity != null && topTileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, EnumFacing.DOWN)) {
                SmokeStorage smokeStorage = topTileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, EnumFacing.DOWN);
                if(smokeStorage != null && smokeStorage.canReceive() &&
                        (!smokeStorage.overloaded() || (smokeStorage.overloaded() && smokeHandler.overloaded()))
                        && smokeHandler.getSmokeAmount() > smokeStorage.getSmokeAmount()) {
                    int delete = smokeHandler.extract(100, false);
                    smokeStorage.receive(delete);
                    SmokeHandlerSynchronizer.synchronize(world, pos.offset(EnumFacing.UP), EnumFacing.DOWN);
                    topInsert = true;
                }
            }
            //If can't insert into top, insert into bottom
            if(!topInsert) {
                TileEntity bottomTileEntity = world.getTileEntity(pos.offset(EnumFacing.DOWN));
                if(bottomTileEntity != null && bottomTileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, EnumFacing.UP)) {
                    SmokeStorage smokeStorage = bottomTileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, EnumFacing.UP);
                    if(smokeStorage != null && smokeStorage.canReceive() && (!smokeStorage.overloaded() || (smokeStorage.overloaded() && smokeHandler.overloaded()))
                            && smokeHandler.getSmokeAmount() > smokeStorage.getSmokeAmount()) {
                        int delete = smokeHandler.extract(20, false);
                        smokeStorage.receive(delete);
                        SmokeHandlerSynchronizer.synchronize(world, pos.offset(EnumFacing.DOWN), EnumFacing.UP);
                    }
                }
            }
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        smokeHandler.deserializeNBT((NBTTagInt) compound.getTag("smoke"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        NBTTagCompound compound = super.writeToNBT(c);
        compound.setTag("smoke", smokeHandler.serializeNBT());
        return compound;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, serializeNBT());
    }
}
