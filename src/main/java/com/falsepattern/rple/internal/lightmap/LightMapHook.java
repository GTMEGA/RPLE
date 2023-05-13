/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.lightmap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.falsepattern.rple.api.lightmap.LightMapChannel;
import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.Utils;
import lombok.*;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.*;
import shadersmod.client.Shaders;

import static net.minecraft.client.Minecraft.getMinecraft;

@SideOnly(Side.CLIENT)
public class LightMapHook {
    private static final int TEXTURE_SIZE = 16;
    private static final float TEXTURE_SCALE = 1F / 256F;
    private static final float TEXTURE_TRANSLATION = 8F;

    public static LightMapHook RED_LIGHT_MAP;
    public static LightMapHook GREEN_LIGHT_MAP;
    public static LightMapHook BLUE_LIGHT_MAP;

    public final DynamicTexture texture;
    public final ResourceLocation location;
    public final int[] colors;
    public final int textureUnit;
    public final int shaderTextureUnit;

    public LightMapHook(int textureUnit, int shaderTextureUnit) {
        this.texture = new DynamicTexture(TEXTURE_SIZE, TEXTURE_SIZE);
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
        RED_LIGHT_MAP.enableReconfigure();
        GREEN_LIGHT_MAP.enableReconfigure();
        BLUE_LIGHT_MAP.enableReconfigure();
    }

    public static void enableAll() {
        RED_LIGHT_MAP.enable();
        GREEN_LIGHT_MAP.enable();
        BLUE_LIGHT_MAP.enable();
    }

    public static void disableAll() {
        RED_LIGHT_MAP.disable();
        GREEN_LIGHT_MAP.disable();
        BLUE_LIGHT_MAP.disable();
    }

    public void enableReconfigure() {
        OpenGlHelper.setActiveTexture(textureUnit);

        // Set because the texture coordinates are supplied as shorts (?)
        // Technically only needs to be set once, but Vanilla does this every time the light map is enabled.
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(TEXTURE_SCALE, TEXTURE_SCALE, 0F);
        GL11.glTranslatef(TEXTURE_TRANSLATION, TEXTURE_TRANSLATION, 0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        if (Utils.shadersEnabled())
            OpenGlHelper.setActiveTexture(shaderTextureUnit);

        getTextureManager().bindTexture(location);

        // The Dynamic texture will default these to nearest neighbour/repeat.
        // Removing this would require not using dynamic textures.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        if (Utils.shadersEnabled()) {
            Shaders.enableLightmap();
        }

        // TODO: Test this, because something here causes horrible error spam.
        if (!GL11.glGetBoolean(GL11.GL_TEXTURE_2D))
            GL11.glEnable(GL11.GL_TEXTURE_2D);

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void enable() {
        OpenGlHelper.setActiveTexture(textureUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void disable() {
        OpenGlHelper.setActiveTexture(textureUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void updateLightMap(float partialTickTime) {
        LightMapPipeline.INSTANCE.updateLightMap(partialTickTime);
        val blockLightMap = LightMapPipeline.INSTANCE.getAccumulatorBlock();
        val skyLightMap = LightMapPipeline.INSTANCE.getAccumulatorSky();
        for (int i = 0; i < LightMapChannel.LIGHT_MAP_SIZE * LightMapChannel.LIGHT_MAP_SIZE; i++) {
            val sky = i / LightMapChannel.LIGHT_MAP_SIZE;
            val block = i % LightMapChannel.LIGHT_MAP_SIZE;
            val gamma = getGamma();
            val R = postProcess(blockLightMap.R[block] + skyLightMap.R[sky], gamma);
            val G = postProcess(blockLightMap.G[block] + skyLightMap.G[sky], gamma);
            val B = postProcess(blockLightMap.B[block] + skyLightMap.B[sky], gamma);
            RED_LIGHT_MAP.colors[i] = 0xFF00FFFF | (R << 16);
            GREEN_LIGHT_MAP.colors[i] = 0xFFFF00FF | (G << 8);
            BLUE_LIGHT_MAP.colors[i] = 0xFFFFFF00 | B;
        }
        RED_LIGHT_MAP.texture.updateDynamicTexture();
        GREEN_LIGHT_MAP.texture.updateDynamicTexture();
        BLUE_LIGHT_MAP.texture.updateDynamicTexture();
    }

    private static int postProcess(float x, float gamma) {
        return (int) (clamp(gammaCorrect(clamp(x), gamma)) * 255F);
    }

    private static float getGamma() {
        return getMinecraft().gameSettings.gammaSetting;
    }

    //        if (world != null) {
    //            for (int i = 0; i < 256; ++i) {
    //                val rawSunBrightness = world.getSunBrightness(1F) * 0.95F + 0.05F;
    //                var skyBlue = world.provider.lightBrightnessTable[i / 16] * rawSunBrightness;
    //                val blockRed = world.provider.lightBrightnessTable[i % 16] * (torchFlickerX * 0.1F + 1.5F);
    //
    //                if (world.lastLightningBolt > 0)
    //                    skyBlue = world.provider.lightBrightnessTable[i / 16];
    //
    //                val skyRed = skyBlue * (world.getSunBrightness(1F) * 0.65F + 0.35F);
    //                val skyGreen = skyBlue * (world.getSunBrightness(1F) * 0.65F + 0.35F);
    //                val blockGreen = blockRed * ((blockRed * 0.6F + 0.4F) * 0.6F + 0.4F);
    //                val blockBlue = blockRed * (blockRed * blockRed * 0.6F + 0.4F);
    //
    //                var red = skyRed + blockRed;
    //                var green = skyGreen + blockGreen;
    //                var blue = skyBlue + blockBlue;
    //                red = red * 0.96F + 0.03F;
    //                green = green * 0.96F + 0.03F;
    //                blue = blue * 0.96F + 0.03F;
    //
    //                if (world.provider.dimensionId == 1) {
    //                    red = 0.22F + blockRed * 0.75F;
    //                    green = 0.28F + blockGreen * 0.75F;
    //                    blue = 0.25F + blockGreen * 0.75F;
    //                }
    //
    //                red = clamp(red);
    //                green = clamp(green);
    //                blue = clamp(blue);
    //
    //                val gamma = mc.gameSettings.gammaSetting;
    //                red = gammaCorrect(red, gamma);
    //                green = gammaCorrect(green, gamma);
    //                blue = gammaCorrect(blue, gamma);
    //
    //                red = clamp(red);
    //                green = clamp(green);
    //                blue = clamp(blue);
    //
    //                val iRed = (int) (red * 255F);
    //                val iGreen = (int) (green * 255F);
    //                val iBlue = (int) (blue * 250F);
    //
    //                RED_LIGHT_MAP.colors[i] = 0xFF00FFFF | (iRed << 16);
    //                GREEN_LIGHT_MAP.colors[i] = 0xFFFF00FF | (iGreen << 8);
    //                BLUE_LIGHT_MAP.colors[i] = 0xFFFFFF00 | iBlue;
    //            }
    //
    //            RED_LIGHT_MAP.texture.updateDynamicTexture();
    //            GREEN_LIGHT_MAP.texture.updateDynamicTexture();
    //            BLUE_LIGHT_MAP.texture.updateDynamicTexture();
    //        }

    private static TextureManager getTextureManager() {
        return getMinecraft().getTextureManager();
    }

    private static float clamp(float value) {
        return Math.max(Math.min(value, 1F), 0F);
    }

    private static float gammaCorrect(float color, float gammaSetting) {
        var colorPreGamma = 1F - color;
        colorPreGamma = 1F - colorPreGamma * colorPreGamma * colorPreGamma * colorPreGamma;
        color = color * (1F - gammaSetting) + colorPreGamma * gammaSetting;
        color = color * 0.96F + 0.03F;
        return color;
    }
}
