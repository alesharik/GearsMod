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

package com.alesharik.gearsmod.integration;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

//TODO use own logger
@ThreadSafe
public final class IntegrationManager {
    private static final ModAPIManager MOD_API_MANAGER = ModAPIManager.INSTANCE;

    private static final List<IntegrationModule> modules = new CopyOnWriteArrayList<>();
    private static final AtomicReference<IntegrationStage> stage = new AtomicReference<>(IntegrationStage.NOT_LOADED);

    /**
     * @throws IllegalArgumentException if module not support current side
     */
    public static void addIntegrationModule(@Nonnull IntegrationModule module) {
        if(!module.getInfo().getSide().valid())
            throw new IllegalArgumentException("Module not support this side!");
        modules.add(module);
        syncWithStage(module);
    }

    public static void preInit() {
        if(stage.get() != IntegrationStage.NOT_LOADED)
            throw new IllegalStateException();
        stage.set(IntegrationStage.PRE_INIT);
        modules.forEach(IntegrationManager::preInitModule);
    }

    public static void init() {
        if(stage.get() != IntegrationStage.PRE_INIT)
            throw new IllegalStateException();
        stage.set(IntegrationStage.INIT);
        modules.forEach(IntegrationManager::initModule);
    }

    public static void postInit() {
        if(stage.get() != IntegrationStage.INIT)
            throw new IllegalStateException();
        stage.set(IntegrationStage.POST_INIT);
        modules.forEach(IntegrationManager::postInitModule);
        stage.set(IntegrationStage.COMPLETED);
    }

    public static IntegrationStage getStage() {
        return stage.get();
    }

    private static void syncWithStage(IntegrationModule module) {
        if(stage.get() == IntegrationStage.NOT_LOADED)
            return;
        if(stage.get() == IntegrationStage.PRE_INIT) {
            preInitModule(module);
        } else if(stage.get() == IntegrationStage.INIT) {
            preInitModule(module);
            initModule(module);
        } else {
            preInitModule(module);
            initModule(module);
            postInitModule(module);
        }
    }

    static void preInitModule(IntegrationModule module) {
        try {
            checkModule(module);
            module.preInit();
        } catch (ModuleDisabledException | ModNotLoadedException e) {
            FMLLog.log(Level.WARN, e.getMessage());
        } catch (Throwable throwable) {
            FMLLog.log(Level.WARN, throwable, "Module %s throw an exception %s in PreInit stage!", module.getInfo().getDisplayName(), throwable.toString());
        }
    }

    static void initModule(IntegrationModule module) {
        try {
            checkModule(module);
            module.init();
        } catch (ModuleDisabledException | ModNotLoadedException e) {
            FMLLog.log(Level.WARN, e.getMessage());
        } catch (Throwable throwable) {
            FMLLog.log(Level.WARN, throwable, "Module %s throw an exception %s in Init stage!", module.getInfo().getDisplayName(), throwable.toString());
        }
    }

    static void postInitModule(IntegrationModule module) {
        try {
            checkModule(module);
            module.postInit();
        } catch (ModuleDisabledException | ModNotLoadedException e) {
            FMLLog.log(Level.WARN, e.getMessage());
        } catch (Throwable throwable) {
            FMLLog.log(Level.WARN, throwable, "Module %s throw an exception %s in PostInit stage!", module.getInfo().getDisplayName(), throwable.toString());
        }
    }

    static void checkModule(IntegrationModule module) {
        if(!module.isEnabled())
            throw new ModuleDisabledException(module);
        String modId = module.getInfo().getModId();
        if(!(Loader.isModLoaded(modId) || MOD_API_MANAGER.hasAPI(modId)))
            throw new ModNotLoadedException(module);
    }
}
