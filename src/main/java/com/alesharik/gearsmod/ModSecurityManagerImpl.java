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

import com.alesharik.gearsmod.proxy.CommonProxy;
import com.alesharik.gearsmod.util.ModSecurityManager;

import java.security.AccessControlException;

class ModSecurityManagerImpl extends ModSecurityManager {
    ModSecurityManagerImpl() {
        super();
    }

    @Override
    public void checkSetLogger() {
        if(getCallerClass(1).equals(GearsMod.class))
            return;
        throw new AccessControlException("Change logger not allowed!");
    }

    @Override
    public void checkGetLogger() {
        String name = getCallerClass(1).getPackage().getName();
        if("com.alesharik".startsWith(name) || "java".startsWith(name) || "sun".startsWith(name))
            return;
        throw new AccessControlException("Access to logger denied!");
    }

    @Override
    public void checkSetExecutor() {
        if(getCallerClass(1).equals(CommonProxy.class))
            return;
        throw new AccessControlException("Change executor not allowed!");
    }

    @Override
    public void checkExecuteTask() {
        String name = getCallerClass(1).getPackage().getName();
        if("com.alesharik".startsWith(name) || "java".startsWith(name) || "sun".startsWith(name))
            return;
        throw new AccessControlException("Access to executor denied!");
    }
}
