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

package com.alesharik.gearsmod.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.concurrent.ForkJoinPool;

public final class Utils {
    private static final Field clientWorldControllerField;
    private static final ForkJoinPool FJP = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    static {
        try {
            clientWorldControllerField = NetHandlerPlayClient.class.getDeclaredField("clientWorldController");
            clientWorldControllerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    private Utils() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static EnumFacing getEnumFacingFromIndex(int index) {
        for(EnumFacing enumFacing : EnumFacing.values())
            if(enumFacing.getIndex() == index)
                return enumFacing;
        return null;
    }

    public static WorldClient getClientWorldFromClientNetHandler(NetHandlerPlayClient handlerPlayClient) {
        try {
            return (WorldClient) clientWorldControllerField.get(handlerPlayClient);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBlockPos(BlockPos pos, ByteBuf byteBuf) {
        byteBuf.writeInt(pos.getX());
        byteBuf.writeInt(pos.getY());
        byteBuf.writeInt(pos.getZ());
    }

    public static void readBlockPos(BlockPos.MutableBlockPos pos, ByteBuf byteBuf) {
        pos.setPos(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt());
    }

    public static ForkJoinPool getModForkJoinPool() {
        return FJP;
    }
}
