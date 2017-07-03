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

package com.alesharik.gearsmod.material;

import net.minecraft.block.material.MapColor;

public final class ModMaterials {
    public static final WrenchableMaterial BRASS_MATERIAL = new BrassMaterial();

    private static final class BrassMaterial extends WrenchableMaterial {
        public BrassMaterial() {
            super(MapColor.GOLD);
            setRequiresTool();
            setImmovableMobility();
        }

        @Override
        public boolean getCanBurn() {
            return false;
        }

        @Override
        public boolean isLiquid() {
            return false;
        }

        @Override
        public boolean isSolid() {
            return true;
        }

        @Override
        public boolean blocksMovement() {
            return true;
        }

        @Override
        public boolean isReplaceable() {
            return false;
        }

        @Override
        public boolean isToolNotRequired() {
            return false;
        }
    }
}
