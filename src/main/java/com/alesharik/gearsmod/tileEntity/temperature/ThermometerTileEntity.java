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

package com.alesharik.gearsmod.tileEntity.temperature;

import com.alesharik.gearsmod.CurrentWorldProvider;
import com.alesharik.gearsmod.NetworkWrapperHolder;
import com.alesharik.gearsmod.temperature.BiomeTemperatureManager;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import com.alesharik.gearsmod.util.Utils;
import com.alesharik.gearsmod.util.provider.TemperatureProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public final class ThermometerTileEntity extends TileEntity implements ITickable {
    private double temperature;
    private double maxTemperature;

    private double lastTemperature;
    private double lastMaxTemperature;

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            BlockPos targetPos = pos.offset(world.getBlockState(pos).getValue(BlockHorizontal.FACING));
            TileEntity targetTileEntity = world.getTileEntity(targetPos);
            if(targetTileEntity != null && targetTileEntity instanceof TemperatureProvider) {
                TemperatureProvider temperatureProvider = (TemperatureProvider) targetTileEntity;
                temperature = temperatureProvider.getTemperature();
                maxTemperature = temperatureProvider.getMaxTemperature();
            } else {
                maxTemperature = 150;
                temperature = BiomeTemperatureManager.getTemperatureManager(world.getBiome(pos), world).getTemperatureSmart(world, pos, true);
            }

            if(lastMaxTemperature != maxTemperature || lastTemperature != temperature) {
                NetworkWrapperHolder.getNetworkWrapper().sendToDimension(new UpdateMessage(pos, world, temperature, maxTemperature), world.provider.getDimension());
                lastMaxTemperature = maxTemperature;
                lastTemperature = temperature;
            }
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public EnumFacing getFacing() {
        return world.getBlockState(pos).getValue(BlockHorizontal.FACING);
    }

    public static final class UpdateMessage implements IMessage {
        private BlockPos.MutableBlockPos pos;
        private int world;
        private double temperature;
        private double maxTemperature;

        public UpdateMessage(BlockPos pos, World world, double temperature, double maxTemperature) {
            this.pos = new BlockPos.MutableBlockPos(pos);
            this.world = world.provider.getDimension();
            this.temperature = temperature;
            this.maxTemperature = maxTemperature;
        }

        public UpdateMessage() {
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = new BlockPos.MutableBlockPos();
            Utils.readBlockPos(pos, buf);
            world = buf.readInt();
            temperature = buf.readDouble();
            maxTemperature = buf.readDouble();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            Utils.writeBlockPos(pos, buf);
            buf.writeInt(world);
            buf.writeDouble(temperature);
            buf.writeDouble(maxTemperature);
        }

        public static class Handler implements IMessageHandler<UpdateMessage, IMessage> {

            @Override
            public IMessage onMessage(UpdateMessage message, MessageContext ctx) {
                World world = CurrentWorldProvider.getWorld(ctx, Side.CLIENT);
                if(world.provider.getDimension() != message.world) {
                    ModLoggerHolder.getModLogger().info("[ThermometerBlockUpdateMessage] Detected message with incorrect world id " + message.world + " on dimension " + world.provider.getDimension());
                } else {
                    ThermometerTileEntity thermometerTileEntity = (ThermometerTileEntity) world.getTileEntity(message.pos);
                    if(thermometerTileEntity == null) {
                        ModLoggerHolder.getModLogger().info("TileEntity in world " + message.world + " in " + message.pos.getX() + "/" + message.pos.getY() + "/" + message.pos.getZ() + " must be ThermometerTileEntity!");
                        return null;
                    }
                    thermometerTileEntity.temperature = message.temperature;
                    thermometerTileEntity.maxTemperature = message.maxTemperature;
                }
                return null;
            }
        }
    }
}
