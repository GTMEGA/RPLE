#version 120

#define FSH

#include "/libs/rple.glsl"

uniform sampler2D texture;

varying vec4 glcolor;

void main() {
    vec4 blockLightColor = blockLight();

    vec4 color = glcolor * blockLightColor;

/* DRAWBUFFERS:0 */
    gl_FragData[0] = color; //gcolor
}