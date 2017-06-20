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
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.EnumFacing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ConnectionProperties {
    public static final IProperty<Boolean> CONNECTION_UP = PropertyBool.create("connection_up");
    public static final IProperty<Boolean> CONNECTION_DOWN = PropertyBool.create("connection_down");
    public static final IProperty<Boolean> CONNECTION_NORTH = PropertyBool.create("connection_north");
    public static final IProperty<Boolean> CONNECTION_SOUTH = PropertyBool.create("connection_south");
    public static final IProperty<Boolean> CONNECTION_WEST = PropertyBool.create("connection_west");
    public static final IProperty<Boolean> CONNECTION_EAST = PropertyBool.create("connection_east");

    public static final Map<EnumFacing, IProperty<Boolean>> CONNECTIONS;

    static {
        Map<EnumFacing, IProperty<Boolean>> map = new HashMap<>();
        map.put(EnumFacing.UP, CONNECTION_UP);
        map.put(EnumFacing.DOWN, CONNECTION_DOWN);
        map.put(EnumFacing.NORTH, CONNECTION_NORTH);
        map.put(EnumFacing.SOUTH, CONNECTION_SOUTH);
        map.put(EnumFacing.EAST, CONNECTION_EAST);
        map.put(EnumFacing.WEST, CONNECTION_WEST);
        CONNECTIONS = Collections.unmodifiableMap(map);
    }

    private ConnectionProperties() {
        throw new UnsupportedOperationException();
    }
}
