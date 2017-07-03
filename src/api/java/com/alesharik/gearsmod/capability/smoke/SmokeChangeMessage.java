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

import com.alesharik.gearsmod.util.ExecutionUtils;
import com.alesharik.gearsmod.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public final class SmokeChangeMessage implements IMessage {
    private int amount;
    private int x;
    private int y;
    private int z;
    private int worldId;
    private int facing;

    public SmokeChangeMessage(int amount, BlockPos blockPos, World worldId, EnumFacing facing) {
        this.amount = amount;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
        this.worldId = worldId.provider.getDimension();
        this.facing = facing == null ? -1 : facing.getIndex();
    }

    public SmokeChangeMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        amount = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        worldId = buf.readInt();
        facing = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(amount);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(worldId);
        buf.writeInt(facing);
    }

    public static final class Handler implements IMessageHandler<SmokeChangeMessage, IMessage> {

        @Override
        public IMessage onMessage(SmokeChangeMessage message, MessageContext ctx) {
            ExecutionUtils.executeTask(ctx, () -> handleMessage(message, ctx));
            return null;
        }

        private void handleMessage(SmokeChangeMessage message, MessageContext context) {
            World world;
            if(context.side == Side.SERVER)
                world = DimensionManager.getWorld(message.worldId);
            else
                world = Utils.getClientWorldFromClientNetHandler(context.getClientHandler());

            TileEntity tileEntity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
            if(tileEntity == null || !tileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, Utils.getEnumFacingFromIndex(message.facing)))
                return;

            SmokeStorage capability = tileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, Utils.getEnumFacingFromIndex(message.facing));
            if(capability instanceof SmokeHandler) {
                SmokeHandler capability1 = (SmokeHandler) capability;
                capability1.smokeAmount = message.amount;
                if(capability1.listener != null)
                    capability1.listener.update(message.amount);
            } else
                throw new IllegalArgumentException("SmokeCapability must extends from SmokeHandler!");
        }
    }
}
