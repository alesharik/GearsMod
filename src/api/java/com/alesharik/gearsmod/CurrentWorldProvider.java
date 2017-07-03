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

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.atomic.AtomicReference;

public final class CurrentWorldProvider {
    static final AtomicReference<Provider> provider = new AtomicReference<>();

    private CurrentWorldProvider() {
        throw new UnsupportedOperationException();
    }

    public static World getWorld(MessageContext context, Side side) {
        return provider.get().getWorld(context, side);
    }

    public interface Provider {
        World getWorld(MessageContext context, Side side);
    }
}
