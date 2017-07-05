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

package com.alesharik.gearsmod.util.rotation;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;

public class TransformationMatrix {
    protected static final double RADIAN = 360D / (Math.PI * 2);
    protected final Matrix4f matrix;

    public TransformationMatrix() {
        matrix = new Matrix4f(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public TransformationMatrix(TransformationMatrix matrix) {
        this.matrix = new Matrix4f(matrix.matrix);
    }

    public TransformationMatrix withTranslation(float x, float y, float z) {
        Matrix4f translationMatrix = new Matrix4f(1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1);
        matrix.mul(translationMatrix);
        return this;
    }

    public TransformationMatrix withScale(float x, float y, float z) {
        Matrix4f scaleMatrix = new Matrix4f(x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1);
        matrix.mul(scaleMatrix);
        return this;
    }

    /**
     * @param angleDeg in degrees
     */
    public TransformationMatrix withRotateX(float angleDeg) {
        double angle = angleDeg / RADIAN;
        Matrix4f scaleMatrix = new Matrix4f(1, 0, 0, 0,
                0, (float) Math.cos(angle), (float) Math.sin(angle), 0,
                0, (float) Math.sin(angle) * -1, (float) Math.cos(angle), 0,
                0, 0, 0, 1);
        matrix.mul(scaleMatrix);
        return this;
    }

    /**
     * @param angleDeg in degrees
     */
    public TransformationMatrix withRotateY(float angleDeg) {
        double angle = angleDeg / RADIAN;
        Matrix4f scaleMatrix = new Matrix4f((float) Math.cos(angle), 0, (float) Math.sin(angle), 0,
                0, 1, 0, 0,
                (float) Math.sin(angle) * -1, 0, (float) Math.cos(angle), 0,
                0, 0, 0, 1);
        matrix.mul(scaleMatrix);
        return this;
    }

    /**
     * @param angleDeg in degrees
     */
    public TransformationMatrix withRotateZ(float angleDeg) {
        double angle = angleDeg / RADIAN;
        Matrix4f scaleMatrix = new Matrix4f((float) Math.cos(angle), (float) Math.sin(angle), 0, 0,
                (float) Math.sin(angle) * -1, (float) Math.cos(angle), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
        matrix.mul(scaleMatrix);
        return this;
    }

    public TransformationMatrix beginPivotPoint(Vector3f point) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(point);
        matrix.mul(matrix4f);
        return this;
    }

    public TransformationMatrix endPivotPoint(Vector3f point) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(new Vector3f(point.x * -1, point.y * -1, point.z * -1));
        matrix.mul(matrix4f);
        return this;
    }

    /**
     * Changes vector!
     */
    public Tuple4f transformVector(Tuple4f vector4f) {
        matrix.transform(vector4f);
        return vector4f;
    }
}
