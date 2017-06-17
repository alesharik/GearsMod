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

package com.alesharik.gearsmod.gl.model;

import java.util.ArrayList;
import java.util.List;

public class Face {
    private final List<Coordinate> coords;

    public Face() {
        coords = new ArrayList<>();
    }

    public void addCoordinates(Coordinate coord) {
        coords.add(coord);
    }

    public int size() {
        return coords.size();
    }

    public Coordinate getCoordinates(int i) {
        return coords.get(i);
    }

    public static class Coordinate {
        private final int vertex;
        private final int texture;
        private final int normal;

        public Coordinate(float vertex, float texture, float normal) {
            this.vertex = (int) vertex;
            this.texture = (int) texture;
            this.normal = (int) normal;
        }

        public int getVertex() {
            return vertex;
        }

        public int getTexture() {
            return texture;
        }

        public int getNormal() {
            return normal;
        }
    }
}
