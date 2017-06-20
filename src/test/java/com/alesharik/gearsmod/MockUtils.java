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

package com.alesharik.gearsmod;

import com.alesharik.gearsmod.util.ModSecurityManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

import static com.alesharik.gearsmod.TestUtils.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MockUtils {
    private MockUtils() {
        throw new UnsupportedOperationException();
    }

    public static FMLCommonHandler mockFMLCommonHandler() {
        FMLCommonHandler fmlCommonHandler = mock(FMLCommonHandler.class);
        setStaticFieldContents(FMLCommonHandler.class, "INSTANCE", fmlCommonHandler);
        return fmlCommonHandler;
    }

    public static World mockWorld(int id, boolean remote) {
        World world = mock(World.class);
        setFieldContents(world, "isRemote", remote);

        WorldProvider worldProvider = mock(WorldProvider.class);
        setFieldContents(world, "provider", worldProvider);
        when(worldProvider.getDimension()).thenReturn(id);

        return world;
    }

    public static WorldServer mockWorldServer(int id, boolean remote) {
        WorldServer world = mock(WorldServer.class);
        setFieldContents(world, "isRemote", remote);

        WorldProvider worldProvider = mock(WorldProvider.class);
        setFieldContents(world, "provider", worldProvider);
        when(worldProvider.getDimension()).thenReturn(id);

        return world;
    }

    @SuppressWarnings("unchecked")
    public static void addWorldServerToDimensionList(int id, WorldServer worldServer) {
        getStaticFieldContents(DimensionManager.class, "worlds", Hashtable.class).put(id, worldServer);
    }

    @SuppressWarnings("unchecked")
    public static ModSecurityManager mockModSecurityManager() {
        try {
            Field field1 = getField(ModSecurityManager.class, "INSTANCE");
            field1.setAccessible(true);
            AtomicReference<ModSecurityManager> securityManagerReference = (AtomicReference<ModSecurityManager>) field1.get(null);

            ModSecurityManager securityManager = mock(ModSecurityManager.class);
            securityManagerReference.set(securityManager);
            return securityManager;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public static void addTileEntityToWorld(World world, BlockPos pos, TileEntity tileEntity) {
        when(world.getTileEntity(pos)).thenReturn(tileEntity);
    }
}
