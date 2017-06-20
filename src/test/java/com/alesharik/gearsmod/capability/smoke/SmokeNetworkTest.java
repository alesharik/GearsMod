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
import com.alesharik.gearsmod.util.ExecutionUtils;
import com.alesharik.gearsmod.util.ModLoggerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.atomic.AtomicReference;

import static com.alesharik.gearsmod.MockUtils.*;
import static com.alesharik.gearsmod.TestUtils.assertUtilityClass;
import static com.alesharik.gearsmod.TestUtils.getStaticFieldContents;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SmokeNetworkTest {
    private Logger logger = mock(Logger.class);
    private SimpleNetworkWrapper networkWrapper = mock(SimpleNetworkWrapper.class);
    private FMLCommonHandler fmlCommonHandler;
    private ExecutionUtils.Executor executor = mock(ExecutionUtils.Executor.class);

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        getStaticFieldContents(ModLoggerHolder.class, "LOGGER", AtomicReference.class).set(logger);
        getStaticFieldContents(NetworkWrapperHolder.class, "NETWORK_WRAPPER_ATOMIC_REFERENCE", AtomicReference.class).set(networkWrapper);
        getStaticFieldContents(ExecutionUtils.class, "executor", AtomicReference.class).set(executor);
        fmlCommonHandler = mockFMLCommonHandler();
        mockModSecurityManager();
    }

    @Test
    public void utilityClassTest() throws Exception {
        assertUtilityClass(SmokeHandlerSynchronizer.class);
    }

    @Test
    public void testClient() throws Exception {
        WorldServer world = mockWorldServer(1, true);

        WorldServer world2 = mockWorldServer(1, true);
        addWorldServerToDimensionList(1, world2);

        TileEntity tileEntity = mock(TileEntity.class);
        SmokeHandler smokeHandler = new SmokeHandler(100, true, true);
        smokeHandler.receive(500);

        TileEntity tileEntity2 = mock(TileEntity.class);
        SmokeHandler smokeHandler2 = new SmokeHandler(100, true, true);

        when(world.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler);

        when(world2.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity2);
        when(tileEntity2.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity2.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler2);

        when(fmlCommonHandler.getEffectiveSide()).thenReturn(Side.CLIENT);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);

        verify(logger).info("Detected sync message about smoke from client to server!");

        ArgumentCaptor<SmokeChangeMessage> captor = ArgumentCaptor.forClass(SmokeChangeMessage.class);
        verify(networkWrapper).sendToServer(captor.capture());

        SmokeChangeMessage message = captor.getValue();
        ByteBuf byteBuf = ByteBufUtil.threadLocalDirectBuffer();
        message.toBytes(byteBuf);

        SmokeChangeMessage message1 = new SmokeChangeMessage();
        message1.fromBytes(byteBuf);

        MessageContext context = mock(MessageContext.class);
        ArgumentCaptor<MessageContext> ctxCaptor = ArgumentCaptor.forClass(MessageContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        new SmokeChangeMessage.Handler().onMessage(message, context);

        verify(executor).executeTask(ctxCaptor.capture(), runnableCaptor.capture());
        assertEquals(context, ctxCaptor.getValue());

        runnableCaptor.getValue().run();

        assertEquals(500, smokeHandler2.getSmokeAmount());
    }

    @Test
    public void testServer() throws Exception {
        WorldServer world = mockWorldServer(1, true);

        WorldServer world2 = mockWorldServer(1, true);
        addWorldServerToDimensionList(1, world2);

        TileEntity tileEntity = mock(TileEntity.class);
        SmokeHandler smokeHandler = new SmokeHandler(100, true, true);
        smokeHandler.receive(500);

        TileEntity tileEntity2 = mock(TileEntity.class);
        SmokeHandler smokeHandler2 = new SmokeHandler(100, true, true);

        when(world.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler);

        when(world2.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity2);
        when(tileEntity2.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity2.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler2);

        when(fmlCommonHandler.getEffectiveSide()).thenReturn(Side.SERVER);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);

        ArgumentCaptor<SmokeChangeMessage> captor = ArgumentCaptor.forClass(SmokeChangeMessage.class);
        verify(networkWrapper).sendToDimension(captor.capture(), ArgumentCaptor.forClass(Integer.class).capture());

        SmokeChangeMessage message = captor.getValue();
        ByteBuf byteBuf = ByteBufUtil.threadLocalDirectBuffer();
        message.toBytes(byteBuf);

        SmokeChangeMessage message1 = new SmokeChangeMessage();
        message1.fromBytes(byteBuf);

        MessageContext context = mock(MessageContext.class);
        ArgumentCaptor<MessageContext> ctxCaptor = ArgumentCaptor.forClass(MessageContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        new SmokeChangeMessage.Handler().onMessage(message, context);

        verify(executor).executeTask(ctxCaptor.capture(), runnableCaptor.capture());
        assertEquals(context, ctxCaptor.getValue());

        runnableCaptor.getValue().run();

        assertEquals(500, smokeHandler2.getSmokeAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testServerIllegal() throws Exception {
        WorldServer world = mockWorldServer(1, true);

        WorldServer world2 = mockWorldServer(1, true);
        addWorldServerToDimensionList(1, world2);

        TileEntity tileEntity = mock(TileEntity.class);
        SmokeStorage smokeHandler = mock(SmokeStorage.class);

        TileEntity tileEntity2 = mock(TileEntity.class);
        SmokeStorage smokeHandler2 = mock(SmokeStorage.class);

        when(world.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler);

        when(world2.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity2);
        when(tileEntity2.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity2.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler2);

        when(fmlCommonHandler.getEffectiveSide()).thenReturn(Side.SERVER);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);

        ArgumentCaptor<SmokeChangeMessage> captor = ArgumentCaptor.forClass(SmokeChangeMessage.class);
        verify(networkWrapper).sendToDimension(captor.capture(), ArgumentCaptor.forClass(Integer.class).capture());

        SmokeChangeMessage message = captor.getValue();
        ByteBuf byteBuf = ByteBufUtil.threadLocalDirectBuffer();
        message.toBytes(byteBuf);

        SmokeChangeMessage message1 = new SmokeChangeMessage();
        message1.fromBytes(byteBuf);

        MessageContext context = mock(MessageContext.class);
        ArgumentCaptor<MessageContext> ctxCaptor = ArgumentCaptor.forClass(MessageContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        new SmokeChangeMessage.Handler().onMessage(message, context);

        verify(executor).executeTask(ctxCaptor.capture(), runnableCaptor.capture());
        assertEquals(context, ctxCaptor.getValue());

        runnableCaptor.getValue().run();

        assertEquals(500, smokeHandler2.getSmokeAmount());
    }

    @Test
    public void testServerWithTileEntityWithoutCapability() throws Exception {
        WorldServer world = mockWorldServer(1, true);

        WorldServer world2 = mockWorldServer(1, true);
        addWorldServerToDimensionList(1, world2);

        TileEntity tileEntity = mock(TileEntity.class);
        SmokeStorage smokeHandler = mock(SmokeStorage.class);

        TileEntity tileEntity2 = mock(TileEntity.class);

        when(world.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity.getCapability(null, EnumFacing.SOUTH)).thenReturn(smokeHandler);

        when(world2.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(tileEntity2);
        when(tileEntity2.hasCapability(null, EnumFacing.SOUTH)).thenReturn(false);
        when(fmlCommonHandler.getEffectiveSide()).thenReturn(Side.SERVER);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);

        ArgumentCaptor<SmokeChangeMessage> captor = ArgumentCaptor.forClass(SmokeChangeMessage.class);
        verify(networkWrapper).sendToDimension(captor.capture(), ArgumentCaptor.forClass(Integer.class).capture());

        SmokeChangeMessage message = captor.getValue();
        ByteBuf byteBuf = ByteBufUtil.threadLocalDirectBuffer();
        message.toBytes(byteBuf);

        SmokeChangeMessage message1 = new SmokeChangeMessage();
        message1.fromBytes(byteBuf);

        MessageContext context = mock(MessageContext.class);
        ArgumentCaptor<MessageContext> ctxCaptor = ArgumentCaptor.forClass(MessageContext.class);
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        new SmokeChangeMessage.Handler().onMessage(message, context);

        verify(executor).executeTask(ctxCaptor.capture(), runnableCaptor.capture());
        assertEquals(context, ctxCaptor.getValue());

        runnableCaptor.getValue().run();
    }

    @Test
    public void synchronizeTestWithTileEntityWithoutCapability() throws Exception {
        World world = mockWorld(1, false);

        TileEntity tileEntity = mock(TileEntity.class);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(false);
        addTileEntityToWorld(world, new BlockPos(0, 0, 0), tileEntity);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);
        verify(fmlCommonHandler, never()).getEffectiveSide();
    }

    @Test
    public void synchronizeTestWithoutTileEntity() throws Exception {
        World world = mockWorld(1, false);

        when(world.getTileEntity(new BlockPos(0, 0, 0))).thenReturn(null);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);
        verify(fmlCommonHandler, never()).getEffectiveSide();
    }

    @Test
    public void synchronizeTestWithTileEntityAdditionalNullCheck() throws Exception {
        World world = mockWorld(1, false);

        TileEntity tileEntity = mock(TileEntity.class);
        when(tileEntity.hasCapability(null, EnumFacing.SOUTH)).thenReturn(true);
        when(tileEntity.getCapability(null, EnumFacing.SOUTH)).thenReturn(null);
        addTileEntityToWorld(world, new BlockPos(0, 0, 0), tileEntity);

        SmokeHandlerSynchronizer.synchronize(world, new BlockPos(0, 0, 0), EnumFacing.SOUTH);
        verify(fmlCommonHandler, never()).getEffectiveSide();
    }
}