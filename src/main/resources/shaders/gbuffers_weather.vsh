#version 120

#define VSH

#include "/libs/rple.glsl"

varying vec2 texcoord;
varying vec4 glcolor;

void main() {
    setLightMapCoordinates();

    texcoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).xy;
    gl_Position = ftransform();
    glcolor = gl_Color;
}