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

import com.alesharik.gearsmod.TestUtils;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ModLoggerHolderTest {
    private ModSecurityManager securityManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field = ModLoggerHolder.class.getDeclaredField("LOGGER");
        field.setAccessible(true);
        AtomicReference<Logger> reference = (AtomicReference<Logger>) field.get(null);
        reference.set(null);

        Field field1 = ModSecurityManager.class.getDeclaredField("INSTANCE");
        field1.setAccessible(true);
        AtomicReference<ModSecurityManager> securityManagerReference = (AtomicReference<ModSecurityManager>) field1.get(null);
        securityManagerReference.set(null);

        securityManager = mock(ModSecurityManager.class);
        ModSecurityManager.setInstance(securityManager);
    }

    @Test
    public void setLoggerTest() throws Exception {
        Logger logger = mock(Logger.class);
        ModLoggerHolder.setModLogger(logger);

        verify(securityManager).checkSetLogger();
        assertEquals(ModLoggerHolder.getModLogger(), logger);
        verify(securityManager).checkGetLogger();
    }

    @Test(expected = IllegalStateException.class)
    public void multipleSetLoggerTest() throws Exception {
        ModLoggerHolder.setModLogger(mock(Logger.class));
        ModLoggerHolder.setModLogger(mock(Logger.class));
    }

    @Test(expected = LoggerNotInitializedException.class)
    public void getModLoggerTestWithNotInitializedLogger() throws Exception {
        ModLoggerHolder.getModLogger();
    }

    @Test
    public void testUtility() throws Exception {
        TestUtils.assertUtilityClass(ModLoggerHolder.class);
    }
}