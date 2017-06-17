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

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class GlShader {
    private static final int STATE_OK = 1;
    private static final int STATE_DELETED = 16;

    private final int shader;
    private final ShaderType type;

    private int state;

    @ParametersAreNonnullByDefault
    public GlShader(String source, ShaderType type) {
        if(!isSupported())
            throw new IllegalStateException("Shaders not supported!");

        this.type = type;
        this.shader = GL20.glCreateShader(type.type);

        if(this.shader == 0)
            throw new IllegalStateException("Shader cannot be created!");

        GL20.glShaderSource(this.shader, source);
        GL20.glCompileShader(this.shader);

        int compileStatus = GL20.glGetShaderi(this.shader, GL20.GL_COMPILE_STATUS);
        if(compileStatus != GL11.GL_TRUE) {
            int logLength = GL20.glGetShaderi(this.shader, GL20.GL_INFO_LOG_LENGTH);
            String shaderLog = GL20.glGetShaderInfoLog(this.shader, logLength);
            throw new IllegalStateException("Shader compile error: \n" + shaderLog);
        }
        this.state = STATE_OK;
    }

    public static boolean isSupported() {
        ContextCapabilities contextCapabilities = GLContext.getCapabilities();
        return contextCapabilities.GL_ARB_shader_objects && contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader;
    }

    public int getShaderInt() {
        checkState();
        return shader;
    }

    public ShaderType getType() {
        return type;
    }

    public void delete() {
        checkState();

        GL20.glDeleteShader(shader);
        state = STATE_DELETED;
    }

    private void checkState() {
        if(state == STATE_OK)
            return;
        if(state == STATE_DELETED)
            throw new IllegalStateException("Shader already deleted!");
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof GlShader)) return false;

        GlShader glShader = (GlShader) o;

        if(shader != glShader.shader) return false;
        if(state != glShader.state) return false;
        return getType() == glShader.getType();
    }

    @Override
    public int hashCode() {
        int result = shader;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + state;
        return result;
    }

    @Override
    public String toString() {
        return "GlShader{" +
                "shader=" + shader +
                ", type=" + type +
                ", state=" + state +
                '}';
    }

    public enum ShaderType {
        VERTEX(GL20.GL_VERTEX_SHADER),
        FRAGMENT(GL20.GL_FRAGMENT_SHADER);

        private final int type;

        ShaderType(int type) {
            this.type = type;
        }
    }
}
