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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SmokeStorageTest {
    @Test
    public void overloaded() throws Exception {
        SmokeStorage smokeStorage = new SmokeStorage() {
            @Override
            public int getSmokeAmount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getMaxSmokeAmount() {
                return 100;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public int extract(int max, boolean simulate) {
                return 0;
            }

            @Override
            public void receive(int amount) {

            }
        };
        assertTrue(smokeStorage.overloaded());

        SmokeStorage smokeStorage1 = new SmokeStorage() {
            @Override
            public int getSmokeAmount() {
                return 100;
            }

            @Override
            public int getMaxSmokeAmount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return false;
            }

            @Override
            public int extract(int max, boolean simulate) {
                return 0;
            }

            @Override
            public void receive(int amount) {

            }
        };
        assertFalse(smokeStorage1.overloaded());
    }

}