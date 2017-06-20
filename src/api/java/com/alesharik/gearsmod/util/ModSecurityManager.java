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
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class provides access to mod {@link SecurityManager}
 */
public abstract class ModSecurityManager extends SecurityManager {
    private static final AtomicReference<ModSecurityManager> INSTANCE = new AtomicReference<>(null);

    protected ModSecurityManager() {
    }

    /**
     * Return {@link ModSecurityManager} current instance
     *
     * @return current instance
     * @throws SecurityManagerNotInitializedException if {@link ModSecurityManager} hadn't been set
     */
    @Nonnull
    public static ModSecurityManager getInstance() {
        if(INSTANCE.get() == null)
            throw new SecurityManagerNotInitializedException();
        return INSTANCE.get();
    }

    /**
     * DO NOT USE IT
     */
    public static void setInstance(@Nonnull ModSecurityManager manager) {
        if(INSTANCE.get() != null)
            throw new IllegalStateException("Instance already set!");
        INSTANCE.set(manager);
    }

    /**
     * Check access for {@link ModLoggerHolder#setModLogger(Logger)}
     */
    public abstract void checkSetLogger();

    /**
     * Check access for {@link ModLoggerHolder#getModLogger()}
     */
    public abstract void checkGetLogger();

    /**
     * Check access for {@link ExecutionUtils#initialize(ExecutionUtils.Executor)}
     */
    public abstract void checkSetExecutor();

    /**
     * Check access for {@link ExecutionUtils#executeTask(MessageContext, Runnable)}
     */
    public abstract void checkExecuteTask();

    @Nonnull
    protected Class<?> getCallerClass(int depth) {
        return getClassContext()[3 + depth];
    }
}
