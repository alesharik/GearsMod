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

package com.alesharik.gearsmod.capability.smoke;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SmokeHandlerTest {
    private SmokeHandler smokeHandler;

    @Before
    public void setUp() throws Exception {
        smokeHandler = new SmokeHandler(10000, true, true);
    }

    @Test
    public void getSmokeAmountAndReceiveTest() throws Exception {
        assertEquals(0, smokeHandler.getSmokeAmount());

        smokeHandler.receive(1000);
        assertEquals(1000, smokeHandler.getSmokeAmount());

        smokeHandler.receive(Integer.MAX_VALUE / 2);
        assertEquals(1000 + Integer.MAX_VALUE / 2, smokeHandler.getSmokeAmount());
    }

    @Test
    public void testFinalValues() throws Exception {
        assertEquals(10000, smokeHandler.getMaxSmokeAmount());
        assertTrue(smokeHandler.canExtract());
        assertTrue(smokeHandler.canReceive());
    }

    @Test
    public void testExtractAndReceiveDisabled() throws Exception {
        SmokeHandler smokeHandler = new SmokeHandler(100, false, false);
        smokeHandler.smokeAmount = 50;

        assertEquals(50, smokeHandler.getSmokeAmount());
        assertEquals(100, smokeHandler.getMaxSmokeAmount());
        assertFalse(smokeHandler.canReceive());
        assertFalse(smokeHandler.canExtract());

        smokeHandler.receive(1000);
        assertEquals(50, smokeHandler.getSmokeAmount());

        int extract = smokeHandler.extract(100, false);
        assertEquals(0, extract);
        assertEquals(50, smokeHandler.getSmokeAmount());
    }

    @Test
    public void extract() throws Exception {
        smokeHandler.receive(1000);

        int extract = smokeHandler.extract(500, false);
        assertEquals(500, extract);
        assertEquals(500, smokeHandler.getSmokeAmount());

        int extract1 = smokeHandler.extract(1000, true);
        assertEquals(500, extract1);
        assertEquals(500, smokeHandler.getSmokeAmount());

        int extract2 = smokeHandler.extract(1000, false);
        assertEquals(500, extract2);
        assertEquals(0, smokeHandler.getSmokeAmount());

        int extract3 = smokeHandler.extract(1000, false);
        assertEquals(0, extract3);
        assertEquals(0, smokeHandler.getSmokeAmount());
    }
}