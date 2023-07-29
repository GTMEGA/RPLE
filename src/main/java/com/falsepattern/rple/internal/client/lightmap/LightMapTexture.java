package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureScale;
import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureTranslation;
import static com.falsepattern.rple.internal.client.lightmap.LightMapConstants.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class LightMapTexture {
    private static IntBuffer PIXEL_BUFFER;

    private final int textureID;

    private final int colorBitMask;
    private final int fixedTextureUnitBinding;
    private final int shaderTextureSamplerBinding;
    private final int fixedTextureCoordsBinding;
    private final int shaderTextureCoordsBinding;

    public static LightMapTexture createLightMapTexture(ColorChannel channel) {
        if (PIXEL_BUFFER == null) {
            PIXEL_BUFFER = ByteBuffer.allocateDirect(LIGHT_MAP_2D_SIZE * Integer.BYTES)
                                     .order(ByteOrder.nativeOrder())
                                     .asIntBuffer();
        }
        val textureID = GL11.glGenTextures();

        val lastTextureName = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTextureName);

        final int colorBitMask;
        final int fixedTextureUnitBinding;
        final int shaderTextureSamplerBinding;
        final int fixedTextureCoordsBinding;
        final int shaderTextureCoordsBinding;
        switch (channel) {
            default:
            case RED_CHANNEL:
                colorBitMask = R_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                fixedTextureCoordsBinding = R_LIGHT_MAP_FIXED_TEXTURE_COORDS_BINDING;
                shaderTextureCoordsBinding = R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                break;
            case GREEN_CHANNEL:
                colorBitMask = G_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                fixedTextureCoordsBinding = G_LIGHT_MAP_FIXED_TEXTURE_COORDS_BINDING;
                shaderTextureCoordsBinding = G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                break;
            case BLUE_CHANNEL:
                colorBitMask = B_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                fixedTextureCoordsBinding = B_LIGHT_MAP_FIXED_TEXTURE_COORDS_BINDING;
                shaderTextureCoordsBinding = B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                break;
        }
        return new LightMapTexture(textureID,
                                   colorBitMask,
                                   fixedTextureUnitBinding,
                                   shaderTextureSamplerBinding,
                                   fixedTextureCoordsBinding,
                                   shaderTextureCoordsBinding);
    }

    public void update(int[] pixels) {
        PIXEL_BUFFER.clear();
        for (int i = 0; i < LIGHT_MAP_2D_SIZE; i++) {
            PIXEL_BUFFER.put(pixels[i] | colorBitMask);
        }
        PIXEL_BUFFER.flip();

        val lastTextureName = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                          0,
                          GL11.GL_RGBA,
                          16,
                          16,
                          0,
                          GL12.GL_BGRA,
                          GL11.GL_UNSIGNED_BYTE,
                          PIXEL_BUFFER);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTextureName);
    }

    public void bind() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureSamplerBinding);
        } else {
            GL13.glActiveTexture(fixedTextureUnitBinding);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL13.glActiveTexture(lastActiveTexture);
    }

    public void rescale() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        val lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureCoordsBinding);
        } else {
            GL13.glActiveTexture(fixedTextureCoordsBinding);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        val scale = lightMapTextureScale();
        val translation = lightMapTextureTranslation();
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(scale, scale, 1F);
        GL11.glTranslatef(translation, translation, 0F);

        GL11.glMatrixMode(lastMatrixMode);
        GL13.glActiveTexture(lastActiveTexture);
    }
}
