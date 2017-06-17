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

public class SmokeHandler implements SmokeStorage {
    protected final int maxSmokeAmount;
    protected final boolean canExtract;
    protected final boolean canReceive;

    protected int smokeAmount;

    public SmokeHandler(int max, boolean canReceive, boolean canExtract) {
        this.maxSmokeAmount = max;
        this.canReceive = canReceive;
        this.canExtract = canExtract;

        this.smokeAmount = 0;
    }

    @Override
    public int getSmokeAmount() {
        return smokeAmount;
    }

    @Override
    public int getMaxSmokeAmount() {
        return maxSmokeAmount;
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public int extract(int max, boolean simulate) {
        if(!canExtract)
            return 0;

        int amount = Math.min(smokeAmount, max);
        if(!simulate) {
            smokeAmount -= amount;
        }
        return amount;
    }

    @Override
    public void receive(int amount) {
        if(!canReceive)
            return;

        smokeAmount += amount;
    }
}
