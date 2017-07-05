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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ThreadSafe
public final class Config {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static Configuration config;

    private static int thermometerOverlayX;
    private static int thermometerOverlayY;

    private Config() {
        throw new UnsupportedOperationException();
    }

    static void init(File file) {
        lock.writeLock().lock();
        try {
            config = new Configuration(file);

            syncConfig();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * MUST BE EXECUTED UNDER WRITE LOCK!
     */
    private static void syncConfig() {
        syncThermometerConfig();
    }

    /**
     * MUST BE EXECUTED UNDER WRITE LOCK!
     */
    private static void syncThermometerConfig() {
        thermometerOverlayX = config.getInt("overlayX", "thermometer", 36, 0, Integer.MAX_VALUE, "Overlay X coordinate FROM LEFT!");
        thermometerOverlayY = config.getInt("overlayY", "thermometer", 6, 0, Integer.MAX_VALUE, "Overlay Y coordinate");
    }

    public static int getThermometerOverlayX() {
        try {
            lock.readLock().lock();
            return thermometerOverlayX;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static int getThermometerOverlayY() {
        try {
            lock.readLock().lock();
            return thermometerOverlayY;
        } finally {
            lock.readLock().unlock();
        }
    }

    static final class ConfigChangeListener {
        private final String modid;

        ConfigChangeListener(String modid) {
            this.modid = modid;
        }

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(!event.getModID().equals(modid))
                return;

            try {
                lock.writeLock().lock();
                if(config != null)
                    syncConfig();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
