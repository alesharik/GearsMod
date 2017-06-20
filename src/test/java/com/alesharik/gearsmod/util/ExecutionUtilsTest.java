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
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecutionUtilsTest {
    private ModSecurityManager securityManager;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        Field field1 = ModSecurityManager.class.getDeclaredField("INSTANCE");
        field1.setAccessible(true);
        AtomicReference<ModSecurityManager> securityManagerReference = (AtomicReference<ModSecurityManager>) field1.get(null);
        securityManagerReference.set(null);

        securityManager = mock(ModSecurityManager.class);
        ModSecurityManager.setInstance(securityManager);

        Field field = ExecutionUtils.class.getDeclaredField("executor");
        field.setAccessible(true);
        AtomicReference<ExecutionUtils.Executor> executorReference = (AtomicReference<ExecutionUtils.Executor>) field.get(null);
        executorReference.set(null);
    }

    @Test(expected = IllegalStateException.class)
    public void setExecutorTest() throws Exception {
        ExecutionUtils.initialize((context, runnable) -> {
        });
        verify(securityManager).checkSetExecutor();
        ExecutionUtils.initialize((context, runnable) -> {
        });
    }

    @Test
    public void executeTask() throws Exception {
        ExecutionUtils.Executor executor = mock(ExecutionUtils.Executor.class);

        ExecutionUtils.initialize(executor);
        MessageContext messageContext = mock(MessageContext.class);
        Runnable runnable = mock(Runnable.class);

        ExecutionUtils.executeTask(messageContext, runnable);

        verify(securityManager).checkExecuteTask();
        verify(executor).executeTask(messageContext, runnable);
    }

    @Test(expected = ExecutorNotInitializedException.class)
    public void executeWithoutInitializationTest() throws Exception {
        ExecutionUtils.executeTask(mock(MessageContext.class), mock(Runnable.class));
    }
}