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

package com.alesharik.gearsmod.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GuiHandlerTest {
    private EntityPlayer fakePlayer;
    private World world;

    @Before
    public void setUp() throws Exception {
        fakePlayer = mock(FakePlayer.class);
        world = mock(World.class);
    }

    @Test
    public void getInstanceTest() throws Exception {
        assertNotNull(GuiHandler.getInstance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerFactoryTest() throws Exception {
        GuiHandler.registerFactory(new GuiFactory() {
            @Override
            public int getGuiId() {
                return 0;
            }

            @Nonnull
            @Override
            public Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new Object();
            }

            @Nonnull
            @Override
            public Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new Object();
            }
        });
        GuiHandler.registerFactory(new GuiFactory() {
            @Override
            public int getGuiId() {
                return 0;
            }

            @Nonnull
            @Override
            public Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new Object();
            }

            @Nonnull
            @Override
            public Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new Object();
            }
        });
    }

    @Test
    public void getElementsTest() throws Exception {
        GuiHandler inst = GuiHandler.getInstance();

        assertNull(inst.getClientGuiElement(1, fakePlayer, world, 1, 1, 1));
        assertNull(inst.getServerGuiElement(1, fakePlayer, world, 1, 1, 1));

        GuiHandler.registerFactory(new GuiFactory() {
            @Override
            public int getGuiId() {
                return 1;
            }

            @Nonnull
            @Override
            public Object getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new HashMap<>(1);
            }

            @Nonnull
            @Override
            public Object getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos blockPos) {
                return new ArrayList<Throwable>();
            }
        });

        assertTrue(inst.getClientGuiElement(1, fakePlayer, world, 1, 1, 1) instanceof HashMap);
        assertTrue(inst.getServerGuiElement(1, fakePlayer, world, 1, 1, 1) instanceof ArrayList);
    }
}