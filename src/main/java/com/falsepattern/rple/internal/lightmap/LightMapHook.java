/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.lightmap;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.Compat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import lombok.var;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shadersmod.client.Shaders;

import static com.falsepattern.rple.internal.Common.LIGHT_MAP_1D_SIZE;
import static net.minecraft.client.Minecraft.getMinecraft;

@SideOnly(Side.CLIENT)
public class LightMapHook {
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
        RED_LIGHT_MAP.enableReconfigure();
        GREEN_LIGHT_MAP.enableReconfigure();
        BLUE_LIGHT_MAP.enableReconfigure();

        if (Compat.shadersEnabled())
            Shaders.enableLightmap();
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

    public void enableReconfigure() {
        OpenGlHelper.setActiveTexture(textureUnit);

        // Set because the texture coordinates are supplied as shorts (?)
        // Technically only needs to be set once, but Vanilla does this every time the light map is enabled.
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(TEXTURE_SCALE, TEXTURE_SCALE, TEXTURE_SCALE);
        GL11.glTranslatef(TEXTURE_TRANSLATION, TEXTURE_TRANSLATION, TEXTURE_TRANSLATION);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // I don't know why, I don't know how. I don't even want to know why.
        // But this works. It fixes the GL Error spam when using shaders??
        // If I enable GL_TEXTURE_2D, (WHICH, BTW, HAPPENS IN STOCK OPTIFINE!!)
        // Then all of a sudden, I get a spam of `GL_INVALID_OPERATION` in the log!
        // HOWEVER!! The only documentation about `glEnable()` throwing this error,
        // is if it is called between `glBegin()` and `glEnd()`.
        // Minecraft may be an ancient block game, but it's not stone age enough to use the fucking
        // "Immediate Mode" fixed render pipeline anymore.
        // Only 2 people know why this works: God and Nobody
        // If we want to know why it works, we need to find who asked!
        // Because nobody asked how this works, nobody will ever ask.
        // I think that OpenGL was made by people who asked.
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureUnit);
        } else {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        getTextureManager().bindTexture(location);

        // The Dynamic texture will default these to nearest neighbour/repeat.
        // Removing this would require not using dynamic textures.
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void updateLightMap(float partialTick) {
        val lightMapRGB = LightMapPipeline.lightMapPipeline().updateLightMap(partialTick);

        for (var i = 0; i < Common.LIGHT_MAP_2D_SIZE; i++) {
            val colorRGB = lightMapRGB[i];
            RED_LIGHT_MAP.colors[i] = colorRGB | 0xFF00FFFF;
            GREEN_LIGHT_MAP.colors[i] = colorRGB | 0xFFFF00FF;
            BLUE_LIGHT_MAP.colors[i] = colorRGB | 0xFFFFFF00;
        }

        RED_LIGHT_MAP.texture.updateDynamicTexture();
        GREEN_LIGHT_MAP.texture.updateDynamicTexture();
        BLUE_LIGHT_MAP.texture.updateDynamicTexture();
    }

    private static TextureManager getTextureManager() {
        return getMinecraft().getTextureManager();
    }
}
