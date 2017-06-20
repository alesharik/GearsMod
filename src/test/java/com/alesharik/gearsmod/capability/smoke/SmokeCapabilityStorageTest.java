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

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SmokeCapabilityStorageTest {
    private SmokeCapabilityStorage smokeCapabilityStorage;

    @Before
    public void setUp() throws Exception {
        smokeCapabilityStorage = new SmokeCapabilityStorage();
    }

    @Test
    public void testNBTSerialization() throws Exception {
        SmokeHandler smokeHandler = new SmokeHandler(5000, true, true);
        smokeHandler.receive(1000);


        NBTBase base = smokeCapabilityStorage.writeNBT(null, smokeHandler, null);

        SmokeHandler n = new SmokeHandler(5000, true, true);
        smokeCapabilityStorage.readNBT(null, n, null, base);
        assertEquals(1000, n.getSmokeAmount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNBTDeserializationWithCustomRealisation() throws Exception {
        smokeCapabilityStorage.readNBT(null, new SmokeStorage() {
            @Override
            public int getSmokeAmount() {
                return 0;
            }

            @Override
            public int getMaxSmokeAmount() {
                return 0;
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
        }, null, new NBTTagInt(1));
    }
}