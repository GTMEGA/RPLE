package com.falsepattern.rple.internal.mixin.extension;

import lombok.*;
import lombok.experimental.*;


@Setter
@Getter
@Accessors(fluent = true, chain = true)
public final class ShaderVertex {
    // TODO: Unify this with the VertexAPI
    public static final int VERTEX_STRIDE_INTS = 20;

    public static final int POSITION_X_STRIDE_OFFSET = 0;
    public static final int POSITION_Y_STRIDE_OFFSET = 1;
    public static final int POSITION_Z_STRIDE_OFFSET = 2;

    public static final int TEXTURE_U_STRIDE_OFFSET = 3;
    public static final int TEXTURE_V_STRIDE_OFFSET = 4;

    public static final int COLOR_STRIDE_OFFSET = 5;
    public static final int LIGHT_MAP_STRIDE_OFFSET = 6;

    public static final int ENTITY_DATA_0_STRIDE_OFFSET = 7;
    public static final int ENTITY_DATA_1_STRIDE_OFFSET = 8;

    public static final int NORMAL_X_STRIDE_OFFSET = 9;
    public static final int NORMAL_Y_STRIDE_OFFSET = 10;
    public static final int NORMAL_Z_STRIDE_OFFSET = 11;

    public static final int TANGENT_X_STRIDE_OFFSET = 12;
    public static final int TANGENT_Y_STRIDE_OFFSET = 13;
    public static final int TANGENT_Z_STRIDE_OFFSET = 14;
    public static final int TANGENT_W_STRIDE_OFFSET = 15;

    public static final int MID_TEXTURE_U_STRIDE_OFFSET = 16;
    public static final int MID_TEXTURE_V_STRIDE_OFFSET = 17;

    public static final int RED_LIGHT_MAP_STRIDE_OFFSET = 6;
    public static final int GREEN_LIGHT_MAP_STRIDE_OFFSET = 18;
    public static final int BLUE_LIGHT_MAP_STRIDE_OFFSET = 19;

    private float positionX;
    private float positionY;
    private float positionZ;

    private float textureU;
    private float textureV;

    private int colorARGB;
    private int lightMapUV;

    private int entityData;
    private int entityData2;

    private float normalX;
    private float normalY;
    private float normalZ;

    private float tangentX;
    private float tangentY;
    private float tangentZ;
    private float tangentW;

    private float midTextureU;
    private float midTextureV;

    private int greenLightMapUV;
    private int blueLightMapUV;

    public ShaderVertex textureU(double textureU) {
        this.textureU = (float) textureU;

        return this;
    }

    public ShaderVertex textureV(double textureV) {
        this.textureV = (float) textureV;

        return this;
    }

    public ShaderVertex redLightMapUV(int redLightMapUV) {
        this.lightMapUV = redLightMapUV;

        return this;
    }

    public int redLightMapUV() {
        return lightMapUV;
    }

    public void toIntArray(int index, int[] output) {
        output[POSITION_X_STRIDE_OFFSET + index] = Float.floatToRawIntBits(positionX);
        output[POSITION_Y_STRIDE_OFFSET + index] = Float.floatToRawIntBits(positionY);
        output[POSITION_Z_STRIDE_OFFSET + index] = Float.floatToRawIntBits(positionZ);

        output[TEXTURE_U_STRIDE_OFFSET + index] = Float.floatToRawIntBits(textureU);
        output[TEXTURE_V_STRIDE_OFFSET + index] = Float.floatToRawIntBits(textureV);

        output[COLOR_STRIDE_OFFSET + index] = colorARGB;
        output[LIGHT_MAP_STRIDE_OFFSET + index] = lightMapUV;

        output[ENTITY_DATA_0_STRIDE_OFFSET + index] = Float.floatToRawIntBits(entityData);
        output[ENTITY_DATA_1_STRIDE_OFFSET + index] = Float.floatToRawIntBits(entityData2);

        output[NORMAL_X_STRIDE_OFFSET + index] = Float.floatToRawIntBits(normalX);
        output[NORMAL_Y_STRIDE_OFFSET + index] = Float.floatToRawIntBits(normalY);
        output[NORMAL_Z_STRIDE_OFFSET + index] = Float.floatToRawIntBits(normalZ);

        output[TANGENT_X_STRIDE_OFFSET + index] = Float.floatToRawIntBits(tangentX);
        output[TANGENT_Y_STRIDE_OFFSET + index] = Float.floatToRawIntBits(tangentY);
        output[TANGENT_Z_STRIDE_OFFSET + index] = Float.floatToRawIntBits(tangentZ);
        output[TANGENT_W_STRIDE_OFFSET + index] = Float.floatToRawIntBits(tangentW);

        output[MID_TEXTURE_U_STRIDE_OFFSET + index] = Float.floatToRawIntBits(midTextureU);
        output[MID_TEXTURE_V_STRIDE_OFFSET + index] = Float.floatToRawIntBits(midTextureV);

        output[GREEN_LIGHT_MAP_STRIDE_OFFSET + index] = greenLightMapUV;
        output[BLUE_LIGHT_MAP_STRIDE_OFFSET + index] = blueLightMapUV;

        clear();
    }

    private void clear() {
        positionX = 0F;
        positionY = 0F;
        positionZ = 0F;

        textureU = 0F;
        textureV = 0F;

        colorARGB = 0;
        lightMapUV = 0;

        entityData = 0;
        entityData2 = 0;

        normalX = 0F;
        normalY = 0F;
        normalZ = 0F;

        tangentX = 0F;
        tangentY = 0F;
        tangentZ = 0F;
        tangentW = 0F;

        midTextureU = 0F;
        midTextureV = 0F;

        greenLightMapUV = 0;
        blueLightMapUV = 0;
    }
}
