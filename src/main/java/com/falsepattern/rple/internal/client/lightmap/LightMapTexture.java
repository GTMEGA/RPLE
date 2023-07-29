package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import lombok.experimental.Accessors;
import lombok.val;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureScale;
import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureTranslation;
import static com.falsepattern.rple.internal.client.lightmap.LightMapConstants.*;

@Accessors(fluent = true, chain = false)
public final class LightMapTexture {
    private static IntBuffer PIXEL_BUFFER;

    private ColorChannel channel;
    private int fixedTextureUnitBinding;
    private int shaderTextureSamplerBinding;
    private int fixedTextureCoordsBinding;
    private int shaderTextureCoordsBinding;

    private final int[] pixels;
    private int textureID;

    public LightMapTexture(ColorChannel channel) {
        pixels = new int[LIGHT_MAP_2D_SIZE];
    }

    public void initBuffer() {
        PIXEL_BUFFER = ByteBuffer.allocateDirect(LIGHT_MAP_2D_SIZE * Integer.BYTES)
                                 .order(ByteOrder.nativeOrder())
                                 .asIntBuffer();
    }

    public void init() {
        val lastTextureName = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

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

    public void setColor(int blockIndex, int skyIndex, int color) {
        switch (channel) {
            default:
            case RED_CHANNEL:
                color |= R_LIGHT_MAP_COLOR_BIT_MASK;
                break;
            case GREEN_CHANNEL:
                color |= G_LIGHT_MAP_COLOR_BIT_MASK;
                break;
            case BLUE_CHANNEL:
                color |= B_LIGHT_MAP_COLOR_BIT_MASK;
                break;
        }
        val index = blockIndex + (skyIndex * LIGHT_MAP_1D_SIZE);
        pixels[index] = color;
    }

    public void upload() {
        PIXEL_BUFFER.clear();
        PIXEL_BUFFER.put(pixels);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                          0,
                          GL11.GL_RGBA,
                          16,
                          16,
                          0,
                          GL11.GL_RGBA,
                          GL11.GL_UNSIGNED_BYTE,
                          PIXEL_BUFFER);
    }

    public void rescale() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        val lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureSamplerBinding);
        } else {
            GL13.glActiveTexture(fixedTextureUnitBinding);
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
