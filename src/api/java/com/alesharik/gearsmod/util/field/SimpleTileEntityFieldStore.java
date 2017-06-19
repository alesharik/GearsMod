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

package com.alesharik.gearsmod.util.field;

import com.alesharik.gearsmod.tileEntity.FieldTileEntity;
import com.alesharik.gearsmod.util.ExecutionUtils;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class require {@link com.alesharik.gearsmod.tileEntity.FieldTileEntity} as TileEntity
 */
public final class SimpleTileEntityFieldStore implements FieldStore {
    private final BlockPos coords;
    private final int dimensionId;
    private final SimpleNetworkWrapper networkWrapper;

    private final TIntIntMap map;

    public SimpleTileEntityFieldStore(BlockPos coords, World world, SimpleNetworkWrapper networkWrapper) {
        this.coords = coords;
        this.networkWrapper = networkWrapper;
        this.dimensionId = world.provider.getDimension();
        this.map = new TIntIntHashMap();
    }

    @Override
    public void setField(int id, int field) {
        map.put(id, field);
        sync();
    }

    @Override
    public int getField(int id) {
        if(!map.containsKey(id))
            throw new FieldNotFoundException();
        return map.get(id);
    }

    @Override
    public boolean containsField(int id) {
        return map.containsKey(id);
    }

    @Override
    public void deleteField(int id) {
        map.remove(id);
        sync();
    }

    @Override
    public void sync() {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            networkWrapper.sendToDimension(new FieldUpdateMessage(coords, dimensionId, map.keys(), map.values()), dimensionId);
        } else {
            networkWrapper.sendToServer(new FieldUpdateMessage(coords, dimensionId, map.keys(), map.values()));
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("keys", new NBTTagIntArray(map.keys()));
        compound.setTag("values", new NBTTagIntArray(map.values()));
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        map.clear();
        int[] keys = ((NBTTagIntArray) nbt.getTag("keys")).getIntArray();
        int[] vales = ((NBTTagIntArray) nbt.getTag("values")).getIntArray();
        for(int i = 0; i < keys.length; i++) {
            map.put(keys[i], vales[i]);
        }
    }

    public static final class FieldUpdateMessage implements IMessage {
        private int x;
        private int y;
        private int z;
        private int worldId;
        private int[] ids;
        private int[] values;

        FieldUpdateMessage(BlockPos blockPos, int worldId, int[] ids, int[] values) {
            this.x = blockPos.getX();
            this.y = blockPos.getY();
            this.z = blockPos.getZ();
            this.worldId = worldId;
            this.ids = ids;
            this.values = values;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
            worldId = buf.readInt();
            int idsCount = buf.readInt();
            ids = new int[idsCount];
            for(int i = 0; i < idsCount; i++) {
                ids[i] = buf.readInt();
            }
            int valuesCount = buf.readInt();
            values = new int[valuesCount];
            for(int i = 0; i < valuesCount; i++) {
                values[i] = buf.readInt();
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
            buf.writeInt(worldId);
            buf.writeInt(ids.length);
            for(int id : ids) {
                buf.writeInt(id);
            }
            buf.writeInt(values.length);
            for(int value : values) {
                buf.writeInt(value);
            }
        }

        public static final class Handler implements IMessageHandler<FieldUpdateMessage, IMessage> {

            @Override
            public IMessage onMessage(FieldUpdateMessage message, MessageContext ctx) {
                ExecutionUtils.executeTask(ctx, () -> handleMessage(message));
                return null;
            }

            private void handleMessage(FieldUpdateMessage message) {
                World world = DimensionManager.getWorld(message.worldId);
                TileEntity tileEntity = world.getTileEntity(new BlockPos(message.x, message.y, message.z));
                if(tileEntity != null && tileEntity instanceof FieldTileEntity) {
                    FieldTileEntity te = (FieldTileEntity) tileEntity;
                    FieldStore fieldStore = te.getFieldStore();
                    int[] ids = message.ids;
                    int[] values = message.values;
                    for(int i = 0; i < ids.length; i++) {
                        fieldStore.setField(ids[i], values[i]);
                    }
                }
            }
        }
    }
}
