/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.Compat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import lombok.var;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import shadersmod.client.Shaders;

import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureScale;
import static com.falsepattern.rple.api.client.RPLELightMapUtil.lightMapTextureTranslation;
import static com.falsepattern.rple.internal.Common.LIGHT_MAP_1D_SIZE;
import static net.minecraft.client.Minecraft.getMinecraft;

@SideOnly(Side.CLIENT)
public final class LightMapHook {
    private static final float TEXTURE_SCALE = 1F / 256F;
    private static final float TEXTURE_TRANSLATION = 8F;

    // TODO: [PRE-RELEASE] Do not use public fields.
    public static LightMapHook RED_LIGHT_MAP;
    public static LightMapHook GREEN_LIGHT_MAP;
    public static LightMapHook BLUE_LIGHT_MAP;

    public final DynamicTexture texture;
    public final ResourceLocation location;
    public final int[] colors;
    public final int textureUnit;
    public final int shaderTextureUnit;

    public LightMapHook(int textureUnit, int shaderTextureUnit) {
        this.texture = new DynamicTexture(LIGHT_MAP_1D_SIZE, LIGHT_MAP_1D_SIZE);
        this.location = getTextureManager().getDynamicTextureLocation("lightMap", texture);
        this.colors = texture.getTextureData();
        this.textureUnit = textureUnit;
        this.shaderTextureUnit = shaderTextureUnit;
    }

    public LightMapHook(DynamicTexture texture,
                        ResourceLocation location,
                        int[] colors,
                        int textureUnit,
                        int shaderTextureUnit) {
        this.texture = texture;
        this.location = location;
        this.colors = colors;
        this.textureUnit = textureUnit;
        this.shaderTextureUnit = shaderTextureUnit;
    }

    public static void init(LightMapHook vanilla) {
        RED_LIGHT_MAP = vanilla;
        GREEN_LIGHT_MAP = new LightMapHook(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, Common.GREEN_LIGHT_MAP_SHADER_TEXTURE_UNIT);
        BLUE_LIGHT_MAP = new LightMapHook(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, Common.BLUE_LIGHT_MAP_SHADER_TEXTURE_UNIT);
    }

    public static void enableReconfigureAll() {
        if (Compat.shadersEnabled())
            Shaders.enableLightmap();

        RED_LIGHT_MAP.prepare();
        GREEN_LIGHT_MAP.prepare();
        BLUE_LIGHT_MAP.prepare();

        if (Compat.shadersEnabled()) {
            rescale(GL13.GL_TEXTURE1);
            rescale(GL13.GL_TEXTURE6);
            rescale(GL13.GL_TEXTURE7);
        } else {
            rescale(GL13.GL_TEXTURE1);
            rescale(GL13.GL_TEXTURE2);
            rescale(GL13.GL_TEXTURE3);
        }
    }

    public void prepare() {
        bindTexture();
    }

    private static void rescale(int textureCoordsID) {
        val lightMapTextureScale = lightMapTextureScale();
        val lightMapTextureTranslation = lightMapTextureTranslation();

        GL13.glActiveTexture(textureCoordsID);
        GL11.glMatrixMode(GL11.GL_TEXTURE);

        GL11.glLoadIdentity();
        GL11.glScalef(lightMapTextureScale, lightMapTextureScale, 0F);
        GL11.glTranslatef(lightMapTextureTranslation, lightMapTextureTranslation, 0F);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public void uploadTexture() {
        texture.updateDynamicTexture();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL11.GL_ZERO);
    }

    public void bindTexture() {
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureUnit);
        } else {
            GL13.glActiveTexture(textureUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
    }

    public static void updateLightMap(float partialTick) {
        val lightMapRGB = LightMapPipeline.lightMapPipeline().updateLightMap(partialTick);

        for (var i = 0; i < Common.LIGHT_MAP_2D_SIZE; i++) {
            val colorRGB = lightMapRGB[i];
            RED_LIGHT_MAP.colors[i] = colorRGB | 0xFF00FFFF;
            GREEN_LIGHT_MAP.colors[i] = colorRGB | 0xFFFF00FF;
            BLUE_LIGHT_MAP.colors[i] = colorRGB | 0xFFFFFF00;
        }

        RED_LIGHT_MAP.uploadTexture();
        GREEN_LIGHT_MAP.uploadTexture();
        BLUE_LIGHT_MAP.uploadTexture();
    }

    private static TextureManager getTextureManager() {
        return getMinecraft().getTextureManager();
    }
}
