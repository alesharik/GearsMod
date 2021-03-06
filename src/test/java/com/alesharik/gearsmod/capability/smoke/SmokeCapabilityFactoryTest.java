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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SmokeCapabilityFactoryTest {
    @Test
    public void callTest() throws Exception {
        SmokeStorage smokeHandler = new SmokeCapabilityFactory().call();
        assertFalse(smokeHandler.canExtract());
        assertFalse(smokeHandler.canReceive());
        assertEquals(smokeHandler.getMaxSmokeAmount(), 1000);
        assertEquals(smokeHandler.getSmokeAmount(), 0);
    }
}