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

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class ExecutionUtils {
    private static Executor executor;

    public static void initialize(Executor executor) {
        if(ExecutionUtils.executor != null)
            throw new IllegalStateException();
        ExecutionUtils.executor = executor;
    }

    public static void executeTask(MessageContext context, Runnable runnable) {
        executor.executeTask(context, runnable);
    }

    public interface Executor {
        void executeTask(MessageContext context, Runnable runnable);
    }
}
