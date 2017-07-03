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

public interface SmokeStorage {
    int getSmokeAmount();

    int getMaxSmokeAmount();

    boolean canExtract();

    boolean canReceive();

    /**
     * Return true if smoke amount is greater than max smoke amount
     */
    default boolean overloaded() {
        return getMaxSmokeAmount() <= getSmokeAmount();
    }

    /**
     * Extract smoke from handler
     *
     * @param max      maximum amount of smoke to extract
     * @param simulate if it is true, process must take no effect on real smoke amount
     * @return how much smoke was extracted
     */
    int extract(int max, boolean simulate);

    /**
     * Insert smoke in device
     *
     * @param amount amount of smoke to insert
     */
    void receive(int amount);
}
