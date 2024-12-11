// Varyings for passing the coords from vertex to fragment stage.
#ifdef RPLE

varying vec2 lmcoordRed;
varying vec2 lmcoordGreen;
varying vec2 lmcoordBlue;

#else

varying vec2 lmcoord;

#endif

#ifdef VSH

// Internally sets the light map coordinates into `lmcoordRed`, `lmcoordGreen` and `lmcoordBlue`.
void setLightMapCoordinates() {
#ifdef RPLE
    lmcoordRed = (gl_TextureMatrix[1] * gl_MultiTexCoord1).st;
    lmcoordGreen = (gl_TextureMatrix[6] * gl_MultiTexCoord6).st;
    lmcoordBlue = (gl_TextureMatrix[7] * gl_MultiTexCoord7).st;
#else
    lmcoord = (gl_TextureMatrix[1] * gl_MultiTexCoord1).st;
#endif
}

#endif

#ifdef FSH

#ifdef RPLE

// Set in RPLE any time a shader would have a `lightmap` uniform.
uniform sampler2D redLightMap;
uniform sampler2D greenLightMap;
uniform sampler2D blueLightMap;

#else

uniform sampler2D lightmap;

#endif

// Returns the coloured light value of the current block light.
vec4 blockLight() {
#ifdef RPLE
    vec4 redLight = texture2D(redLightMap, lmcoordRed);
    vec4 greenLight = texture2D(greenLightMap, lmcoordGreen);
    vec4 blueLight = texture2D(blueLightMap, lmcoordBlue);
    return redLight * greenLight * blueLight;
#else
    return texture2D(lightmap, lmcoord);
#endif
}

#endif
