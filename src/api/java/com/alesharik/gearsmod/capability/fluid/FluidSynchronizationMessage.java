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

import com.alesharik.gearsmod.CurrentWorldProvider;
import com.alesharik.gearsmod.util.ExecutionUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.alesharik.gearsmod.util.ModLoggerHolder.getModLogger;
import static com.alesharik.gearsmod.util.Utils.getEnumFacingFromIndex;
import static com.alesharik.gearsmod.util.Utils.readBlockPos;

/**
 * Synchronize water amount in {@link SynchronizedFluidTank} and {@link net.minecraftforge.fluids.capability.IFluidHandler}
 */
public final class FluidSynchronizationMessage implements IMessage {
    private final BlockPos.MutableBlockPos blockPos;

    private int worldId;
    private EnumFacing facing;
    private int amount;
    private String fluidName;

    public FluidSynchronizationMessage(@Nonnull BlockPos blockPos, @Nonnull World world, int amount, @Nonnull EnumFacing facing, @Nullable Fluid fluid) {
        this.blockPos = new BlockPos.MutableBlockPos(blockPos);
        this.worldId = world.provider.getDimension();
        this.amount = amount;
        this.facing = facing;
        this.fluidName = fluid == null ? null : fluid.getName();
    }

    public FluidSynchronizationMessage() {
        blockPos = new BlockPos.MutableBlockPos();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        readBlockPos(blockPos, buf);
        worldId = buf.readInt();
        facing = getEnumFacingFromIndex(buf.readByte());

        amount = buf.readInt();
        int count = buf.readInt();
        if(count == -1)
            fluidName = null;
        else {
            byte[] bytes = new byte[count];
            buf.readBytes(bytes, 0, bytes.length);
            fluidName = new String(bytes, Charsets.UTF_8);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(worldId);
        buf.writeByte(facing.getIndex());

        buf.writeInt(amount);
        if(fluidName == null)
            buf.writeInt(-1);
        else {
            byte[] bytes = fluidName.getBytes(Charsets.UTF_8);
            buf.writeInt(bytes.length);
            for(byte aByte : bytes) buf.writeByte(aByte);
        }
    }

    private TileEntity getTileEntity(World world) {
        return world.getTileEntity(blockPos);
    }

    boolean hasCapability(World world) {
        TileEntity tileEntity = getTileEntity(world);
        return tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
    }

    IFluidHandler getCapability(World world) {
        TileEntity tileEntity = getTileEntity(world);
        return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
    }

    public static final class Handler implements IMessageHandler<FluidSynchronizationMessage, IMessage> {

        @Override
        public IMessage onMessage(FluidSynchronizationMessage message, MessageContext ctx) {
            ExecutionUtils.executeTask(ctx, () -> handleMessage(message, ctx));
            return null;
        }

        private void handleMessage(FluidSynchronizationMessage message, MessageContext context) {
            World world;
            if(context.side == Side.SERVER) {
                world = DimensionManager.getWorld(message.worldId);
                getModLogger().log(Level.INFO, "Detected FluidSynchronizationMessage from client to server!");
            } else {
                world = CurrentWorldProvider.getWorld(context, Side.CLIENT);
            }

            if(message.hasCapability(world)) handleCapability(message, world);
        }

        private void handleCapability(FluidSynchronizationMessage message, World world) {
            IFluidHandler capability = message.getCapability(world);
            if(capability == null)
                return;

            if(capability instanceof SynchronizedFluidTank) {
                SynchronizedFluidTank synchronizedFluidTank = (SynchronizedFluidTank) capability;
                if(message.fluidName == null)
                    synchronizedFluidTank.clear();
                else
                    synchronizedFluidTank.setAmount(message.fluidName, message.amount);
            } else {
                for(IFluidTankProperties property : capability.getTankProperties()) {
                    if(message.fluidName == null && property.getContents() != null)
                        capability.drain(property.getContents().amount, true);
                    else if(message.fluidName != null) {
                        FluidStack fluidStack = property.getContents();
                        if(fluidStack != null && fluidStack.getFluid().getName().equals(message.fluidName)) {
                            fluidStack.amount = message.amount;
                        }
                    }
                }
            }
        }
    }
}
