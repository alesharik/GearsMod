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

package com.alesharik.gearsmod.capability.fluid;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.alesharik.gearsmod.util.ModLoggerHolder.getModLogger;
import static com.alesharik.gearsmod.util.Utils.getEnumFacingFromIndex;
import static com.alesharik.gearsmod.util.Utils.writeBlockPos;

public final class RequestFluidSynchronizationMessage implements IMessage {
    private final BlockPos.MutableBlockPos blockPos;
    private int worldId;
    private EnumFacing facing;

    public RequestFluidSynchronizationMessage(@Nonnull BlockPos blockPos, @Nonnull World world, @Nullable EnumFacing facing) {
        this.blockPos = new BlockPos.MutableBlockPos(blockPos);
        this.worldId = world.provider.getDimension();
        this.facing = facing;
    }

    public RequestFluidSynchronizationMessage() {
        blockPos = new BlockPos.MutableBlockPos();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos.setPos(buf.readInt(), buf.readInt(), buf.readInt());
        worldId = buf.readInt();
        facing = getEnumFacingFromIndex(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeBlockPos(blockPos, buf);
        buf.writeInt(worldId);
        buf.writeByte(facing == null ? -1 : facing.getIndex());
    }

    public static final class Handler implements IMessageHandler<RequestFluidSynchronizationMessage, IMessage> {

        @Override
        public FluidSynchronizationMessage onMessage(RequestFluidSynchronizationMessage message, MessageContext ctx) {
            if(ctx.side == Side.SERVER) {
                World world = DimensionManager.getWorld(message.worldId);
                TileEntity tileEntity = world.getTileEntity(message.blockPos);
                if(tileEntity == null || !tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, message.facing))
                    return null;

                IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, message.facing);
                if(handler == null)
                    return null;

                IFluidTankProperties[] tankProperties = handler.getTankProperties();
                if(tankProperties.length == 0 || tankProperties[0].getContents() == null)
                    return new FluidSynchronizationMessage(message.blockPos, world, -1, message.facing, null);

                return new FluidSynchronizationMessage(message.blockPos, world, tankProperties[0].getContents().amount, message.facing, tankProperties[0].getContents().getFluid());
            } else {
                getModLogger().log(Level.INFO, "Detected RequestFluidSynchronizationMessage on client!");
                return null;
            }
        }
    }
}
