#version 120

#define VSH

#include "/libs/rple.glsl"

varying vec4 glcolor;

void main() {
    setLightMapCoordinates();

    gl_Position = ftransform();
    glcolor = gl_Color;
}