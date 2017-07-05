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

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class deconstructs {@link net.minecraft.client.renderer.block.model.BakedQuad}'s data
 */
@SideOnly(Side.CLIENT)
public final class BakedQuadDeconstructor {
    private BakedQuadDeconstructor() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage#POSITION}: x, y, z, w
     * {@link net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage#UV}: u, v, 0, 1
     * {@link net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage#COLOR}: r, g, b, a
     * {@link net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage#NORMAL}: x, y, z, 1
     */
    @SideOnly(Side.CLIENT)
    public static BakedQuadInfo deconstruct(BakedQuad quad) {
        int[] vecData = quad.getVertexData();
        int selector = 0;

        List<VertexInfo> vertexes = new ArrayList<>();

        VertexFormat vertexFormat = quad.getFormat();
        while(vecData.length > selector + 1) {
            VertexInfo.Builder builder = new VertexInfo.Builder();
            for(int i = 0; i < vertexFormat.getElementCount(); i++) {
                VertexFormatElement element = vertexFormat.getElement(i);
                VertexFormatElement.EnumUsage usage = element.getUsage();

                if(usage == VertexFormatElement.EnumUsage.POSITION) {
                    float[] data = new float[3];
                    for(int j = 0; j < 3; j++) {
                        data[j] = Float.intBitsToFloat(vecData[selector]);
                        selector++;
                    }
                    builder.setX(data[0]);
                    builder.setY(data[1]);
                    builder.setZ(data[2]);
                    builder.setW(data.length >= 4 ? data[3] : 1);
                } else if(usage == VertexFormatElement.EnumUsage.COLOR) {
                    int rgba = vecData[selector];
                    selector++;

                    int r = rgba & 0xFF;
                    int g = (rgba >> 8) & 0xFF;
                    int b = (rgba >> 16) & 0xFF;
                    int a = (rgba >>> 24) & 0xFF;
                    builder.setColor(new Color(r, g, b, a));
                } else if(usage == VertexFormatElement.EnumUsage.UV) {
                    float[] data = new float[2];
                    for(int j = 0; j < 2; j++) {
                        data[j] = Float.intBitsToFloat(vecData[selector]);
                        selector++;
                    }
                    builder.setU(data[0]);
                    builder.setV(data[1]);
                } else if(usage == VertexFormatElement.EnumUsage.NORMAL) {//Skip padding because of read read 4 bytes there
                    int combined = vecData[selector];
                    selector++;

                    builder.setNormalX(((combined) & 0xFF) / 0x7F);
                    builder.setNormalY(((combined >> 8) & 0xFF) / 0x7F);
                    builder.setNormalZ(((combined >> 16) & 0xFF) / 0x7F);
                }
            }
            vertexes.add(builder.createVertexInfo());
        }
        return new BakedQuadInfo(vertexes);
    }

    public static class BakedQuadInfo {
        private final List<VertexInfo> vertexInfo;

        BakedQuadInfo(List<VertexInfo> vertexInfo) {
            this.vertexInfo = vertexInfo;
        }

        public List<VertexInfo> getVertexInfo() {
            return vertexInfo;
        }
    }

    public static class VertexInfo {
        private final float x;
        private final float y;
        private final float z;
        private final float w;

        private final float u;
        private final float v;

        private final Color color;

        private final int normalX;
        private final int normalY;
        private final int normalZ;

        VertexInfo(float x, float y, float z, float w, float u, float v, Color color, int normalX, int normalY, int normalZ) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.u = u;
            this.v = v;
            this.color = color;
            this.normalX = normalX;
            this.normalY = normalY;
            this.normalZ = normalZ;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public float getW() {
            return w;
        }

        public float getU() {
            return u;
        }

        public float getV() {
            return v;
        }

        public Color getColor() {
            return color;
        }

        public int getNormalX() {
            return normalX;
        }

        public int getNormalY() {
            return normalY;
        }

        public int getNormalZ() {
            return normalZ;
        }

        static final class Builder {
            private float x;
            private float y;
            private float z;
            private float w;
            private float u;
            private float v;
            private Color color;
            private int normalX;
            private int normalY;
            private int normalZ;

            public Builder setX(float x) {
                this.x = x;
                return this;
            }

            public Builder setY(float y) {
                this.y = y;
                return this;
            }

            public Builder setZ(float z) {
                this.z = z;
                return this;
            }

            public Builder setW(float w) {
                this.w = w;
                return this;
            }

            public Builder setU(float u) {
                this.u = u;
                return this;
            }

            public Builder setV(float v) {
                this.v = v;
                return this;
            }

            public Builder setColor(Color color) {
                this.color = color;
                return this;
            }

            public Builder setNormalX(int normalX) {
                this.normalX = normalX;
                return this;
            }

            public Builder setNormalY(int normalY) {
                this.normalY = normalY;
                return this;
            }

            public Builder setNormalZ(int normalZ) {
                this.normalZ = normalZ;
                return this;
            }

            @SideOnly(Side.CLIENT)
            public BakedQuadDeconstructor.VertexInfo createVertexInfo() {
                return new BakedQuadDeconstructor.VertexInfo(x, y, z, w, u, v, color, normalX, normalY, normalZ);
            }
        }
    }
}
