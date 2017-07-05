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

import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class holds {@link Logger} instance for mod
 */
public final class ModLoggerHolder {
    private static final AtomicReference<Logger> LOGGER = new AtomicReference<>(null);

    private ModLoggerHolder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Return mod logger
     *
     * @return mod logger
     * @throws java.security.AccessControlException if caller class doesn't have rights to get logger
     * @throws LoggerNotInitializedException        if logger hadn't been set
     */
    public static Logger getModLogger() {
        ModSecurityManager.getInstance().checkGetLogger();

        Logger logger = LOGGER.get();
        if(logger == null)
            throw new LoggerNotInitializedException();
        return logger;
    }

    /**
     * DO NOT EXECUTE IT
     */
    public static synchronized void setModLogger(@Nonnull Logger logger) {
        ModSecurityManager.getInstance().checkSetLogger();

        if(LOGGER.get() != null)
            throw new IllegalStateException("Logger already exists!");
        LOGGER.set(logger);
    }
}
