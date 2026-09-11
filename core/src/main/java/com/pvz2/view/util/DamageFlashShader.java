package com.pvz2.view.util;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class DamageFlashShader {
    private static ShaderProgram shader;

    private static final String VERTEX =
        "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}\n";

    private static final String FRAGMENT =
        "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform vec3 u_flashColor;\n" +
            "uniform float u_flashAmount;\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(u_texture, v_texCoords) * v_color;\n" +
            "    vec3 mixed = mix(texColor.rgb, u_flashColor, u_flashAmount);\n" +
            "    gl_FragColor = vec4(mixed, texColor.a);\n" +
            "}\n";

    private DamageFlashShader() {}

    public static ShaderProgram get() {
        if (shader == null) {
            ShaderProgram.pedantic = false;
            shader = new ShaderProgram(VERTEX, FRAGMENT);
            if (!shader.isCompiled()) {
                throw new IllegalStateException("DamageFlashShader compile failed: " + shader.getLog());
            }
        }
        return shader;
    }
}
