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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class allows you to execute tasks in client/server thread
 */
@ThreadSafe
public final class ExecutionUtils {
    private static final AtomicReference<Executor> executor = new AtomicReference<>(null);

    /**
     * DO NOT USE THIS
     */
    public static void initialize(@Nonnull Executor executor) {
        ModSecurityManager.getInstance().checkSetExecutor();

        if(ExecutionUtils.executor.get() != null)
            throw new IllegalStateException();
        ExecutionUtils.executor.set(executor);
    }

    /**
     * Execute task on server/client thread
     *
     * @param context  message context
     * @param runnable the task
     * @throws ExecutorNotInitializedException if executor not initialized
     */
    public static void executeTask(@Nonnull MessageContext context, @Nonnull Runnable runnable) {
        ModSecurityManager.getInstance().checkExecuteTask();

        if(executor.get() == null)
            throw new ExecutorNotInitializedException();
        executor.get().executeTask(context, runnable);
    }

    /**
     * This class used for execute tasks
     */
    public interface Executor {
        void executeTask(MessageContext context, Runnable runnable);
    }
}
