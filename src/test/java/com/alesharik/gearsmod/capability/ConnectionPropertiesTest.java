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

package com.alesharik.gearsmod.capability;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import org.junit.Test;

import java.util.Map;

import static com.alesharik.gearsmod.TestUtils.assertUtilityClass;
import static org.junit.Assert.assertEquals;

public class ConnectionPropertiesTest {
    @Test
    public void utilityClassTest() throws Exception {
        assertUtilityClass(ConnectionProperties.class);
    }

    @Test
    public void connectionMapTest() throws Exception {
        Map<EnumFacing, IProperty<Boolean>> connections = ConnectionProperties.CONNECTIONS;

        assertEquals(ConnectionProperties.CONNECTION_DOWN, connections.get(EnumFacing.DOWN));
        assertEquals(ConnectionProperties.CONNECTION_UP, connections.get(EnumFacing.UP));
        assertEquals(ConnectionProperties.CONNECTION_EAST, connections.get(EnumFacing.EAST));
        assertEquals(ConnectionProperties.CONNECTION_WEST, connections.get(EnumFacing.WEST));
        assertEquals(ConnectionProperties.CONNECTION_NORTH, connections.get(EnumFacing.NORTH));
        assertEquals(ConnectionProperties.CONNECTION_SOUTH, connections.get(EnumFacing.SOUTH));
    }
}