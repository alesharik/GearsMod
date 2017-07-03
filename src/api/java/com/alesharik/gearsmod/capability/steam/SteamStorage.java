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

package com.alesharik.gearsmod.capability.steam;

import com.alesharik.gearsmod.steam.SteamNetwork;

import javax.annotation.Nonnull;

public interface SteamStorage {
    @Nonnull
    SteamNetwork getNetwork();

    /**
     * @return max force in MN(mega Newton)
     */
    double getMaxForce();

    /**
     * @param force force in MN(mega Newton)
     */
    void handleOverloadForce(double force);

    /**
     * @return block volume in m3
     */
    double getVolume();
}
