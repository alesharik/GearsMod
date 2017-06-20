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

import net.minecraft.nbt.NBTTagCompound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializableEnergyStorageTest {
    private SerializableEnergyStorage energyStorage;

    @Before
    public void setUp() throws Exception {
        energyStorage = new SerializableEnergyStorage(5000, 100, 100, 500);
    }

    @Test
    public void testConstructors() throws Exception {
        new SerializableEnergyStorage(100);
        new SerializableEnergyStorage(100, 100);
        new SerializableEnergyStorage(100, 100, 100);
        new SerializableEnergyStorage(100, 100, 100, 100);
    }

    @Test
    public void serializeDeserializeNBTTest() throws Exception {
        NBTTagCompound compound = energyStorage.serializeNBT();

        SerializableEnergyStorage energyStorage1 = new SerializableEnergyStorage(5000, 100, 100, 1000);
        energyStorage1.deserializeNBT(compound);

        assertEquals(energyStorage.getEnergyStored(), energyStorage1.getEnergyStored());

        SerializableEnergyStorage energyStorage2 = new SerializableEnergyStorage(1, 100, 100);
        energyStorage2.deserializeNBT(compound);

        assertEquals(1, energyStorage2.getEnergyStored());
    }

    @Test
    public void writeDeserializeNBTTest() throws Exception {
        NBTTagCompound compound = new NBTTagCompound();
        energyStorage.writeToNBT(compound);

        SerializableEnergyStorage energyStorage1 = new SerializableEnergyStorage(5000, 100, 100, 1000);
        energyStorage1.deserializeNBT(compound);

        assertEquals(energyStorage.getEnergyStored(), energyStorage1.getEnergyStored());
    }
}