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

package com.alesharik.gearsmod.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NotThreadSafe
public final class GlProgram {
    private static final int STATE_OK = 1;
    private static final int STATE_LINKED = 2;
    private static final int STATE_DELETED = 3;

    private final List<GlShader> shaders;
    private final int program;

    private int state;

    public GlProgram() {
        if(!GlShader.isSupported())
            throw new IllegalStateException("Not supported!");

        this.shaders = new ArrayList<>();
        this.program = GL20.glCreateProgram();

        this.state = STATE_OK;
    }

    public GlProgram(List<GlShader> shaders) {
        this();
        shaders.forEach(this::addShader);
    }

    public void addShader(GlShader shader) {
        if(state != STATE_OK)
            throw new IllegalStateException();

        this.shaders.add(shader);
        GL20.glAttachShader(program, shader.getShaderInt());
    }

    public void removeShader(GlShader shader) {
        if(state != STATE_OK)
            throw new IllegalStateException();

        if(this.shaders.contains(shader)) {
            this.shaders.remove(shader);
            GL20.glDetachShader(program, shader.getShaderInt());
        }
    }

    public List<GlShader> getShaders() {
        return Collections.unmodifiableList(shaders);
    }

    public void link() {
        if(state != STATE_OK)
            throw new IllegalStateException();
        GL20.glLinkProgram(program);

        int status = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if(status != GL11.GL_TRUE) {
            int logLength = GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH);
            String programLog = GL20.glGetProgramInfoLog(program, logLength);
            throw new IllegalStateException("Linker error: \n" + programLog);
        }

        state = STATE_LINKED;
    }

    public int getProgramInt() {
        return program;
    }

    public void delete() {
        if(state == STATE_DELETED)
            throw new IllegalStateException("Program already deleted!");
        GL20.glDeleteProgram(program);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof GlProgram)) return false;

        GlProgram glProgram = (GlProgram) o;

        if(program != glProgram.program) return false;
        if(state != glProgram.state) return false;
        return shaders != null ? shaders.equals(glProgram.shaders) : glProgram.shaders == null;
    }

    @Override
    public int hashCode() {
        int result = shaders != null ? shaders.hashCode() : 0;
        result = 31 * result + program;
        result = 31 * result + state;
        return result;
    }

    @Override
    public String toString() {
        return "GlProgram{" +
                "shaders=" + shaders +
                ", program=" + program +
                ", state=" + state +
                '}';
    }
}
