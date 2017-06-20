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

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ModSecurityManagerTest {
    private ModSecurityManager securityManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field1 = ModSecurityManager.class.getDeclaredField("INSTANCE");
        field1.setAccessible(true);
        AtomicReference<ModSecurityManager> securityManagerReference = (AtomicReference<ModSecurityManager>) field1.get(null);
        securityManagerReference.set(null);

        securityManager = mock(ModSecurityManager.class);
    }

    @Test
    public void setInstanceTest() throws Exception {
        ModSecurityManager.setInstance(securityManager);
        assertEquals(securityManager, ModSecurityManager.getInstance());
    }

    @Test(expected = IllegalStateException.class)
    public void multipleSetInstanceTest() throws Exception {
        ModSecurityManager.setInstance(securityManager);
        ModSecurityManager.setInstance(securityManager);
    }

    @Test(expected = SecurityManagerNotInitializedException.class)
    public void getInstanceTestWithNotInitializedSecurityManager() throws Exception {
        ModSecurityManager.getInstance();
    }

    @Test
    public void getCallerClassTest() throws Exception {
        ModSecurityManager securityManager = new ModSecurityManager() {
            @Override
            public void checkSetLogger() {

            }

            @Override
            public void checkGetLogger() {

            }

            @Override
            public void checkSetExecutor() {

            }

            @Override
            public void checkExecuteTask() {

            }

            @Override
            public void checkGetNetworkWrapper() {

            }
        };

        Function<Void, Class<?>> function = aVoid -> securityManager.getCallerClass(1);

        assertEquals(ModSecurityManagerTest.class, function.apply(null));
    }

}