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

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@NotThreadSafe
public final class OBJModel implements Model {
    private final List<Vector3f> vertices;
    private final List<Vector3f> normals;
    private final List<Vector2f> texCoords;
    private final List<Face> triangles;
    private final List<Face> rectangles;
    private final List<Face> polygons;

    @ParametersAreNonnullByDefault
    public OBJModel(String[] lines) {
        vertices = new ArrayList<>();
        normals = new ArrayList<>();
        texCoords = new ArrayList<>();
        triangles = new ArrayList<>();
        rectangles = new ArrayList<>();
        polygons = new ArrayList<>();

        Loader.load(this, lines);
    }

    @ParametersAreNonnullByDefault
    public OBJModel(File file) throws IOException {
        this(Files.readAllLines(file.toPath()).toArray(new String[0]));
    }

    @Override
    public void render() {
        glPushMatrix();

        glBegin(GL_TRIANGLES);
        for(Face triangle : triangles) {
            for(int i = 0; i < triangle.size(); i++) {
                Face.Coordinate coordinate = triangle.getCoordinates(i);
                Vector3f vertex = vertices.get(coordinate.getVertex() - 1);
                glVertex3f(vertex.x, vertex.y, vertex.z);

                Vector2f texCoord = texCoords.get(coordinate.getTexture() - 1);
                glTexCoord2f(texCoord.x, texCoord.y);

                Vector3f normal = normals.get(coordinate.getNormal() - 1);
                glNormal3f(normal.x, normal.y, normal.z);
            }
        }
        glEnd();
        glBegin(GL_QUADS);
        for(Face rect : rectangles) {
            for(int i = 0; i < rect.size(); i++) {
                Face.Coordinate coordinate = rect.getCoordinates(i);
                Vector3f vertex = vertices.get(coordinate.getVertex() - 1);
                glVertex3f(vertex.x, vertex.y, vertex.z);

                Vector2f texCoord = texCoords.get(coordinate.getTexture() - 1);
                glTexCoord2f(texCoord.x, texCoord.y);

                Vector3f normal = normals.get(coordinate.getNormal() - 1);
                glNormal3f(normal.x, normal.y, normal.z);
            }
        }
        glEnd();
        for(Face polygon : polygons) {
            glBegin(GL_POLYGON);
            for(int i = 0; i < polygon.size(); i++) {
                Face.Coordinate coordinate = polygon.getCoordinates(i);
                Vector3f vertex = vertices.get(coordinate.getVertex() - 1);
                glVertex3f(vertex.x, vertex.y, vertex.z);

                Vector2f texCoord = texCoords.get(coordinate.getTexture() - 1);
                glTexCoord2f(texCoord.x, texCoord.y);

                Vector3f normal = normals.get(coordinate.getNormal() - 1);
                glNormal3f(normal.x, normal.y, normal.z);
            }
            glEnd();
        }
        glPopMatrix();
    }

    private static final class Loader {
        private static void load(OBJModel model, String[] lines) {
            for(String line : lines) {
                if(line.startsWith("v ")) {
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    model.vertices.add(new Vector3f(x, y, z));
                }
                if(line.startsWith("vn ")) {
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    model.normals.add(new Vector3f(x, y, z));
                }
                if(line.startsWith("vt ")) {
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    model.texCoords.add(new Vector2f(x, y));
                }
                if(line.startsWith("f ")) {
                    Face face = new Face();
                    String[] parts = line.substring(2).split(" ");
                    for(String part : parts) {
                        String[] subParts = part.split("/");
                        float vertex = Float.valueOf(subParts[0]);
                        float texCoord = Float.valueOf(subParts[1]);
                        float normal = Float.valueOf(subParts[2]);
                        face.addCoordinates(new Face.Coordinate(vertex, texCoord, normal));
                    }
                    if(parts.length == 3) {
                        model.triangles.add(face);
                    } else if(parts.length == 4) {
                        model.rectangles.add(face);
                    } else {
                        model.polygons.add(face);
                    }
                }
            }
        }
    }
}
