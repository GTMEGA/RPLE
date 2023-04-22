package com.falsepattern.rple.internal;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LightMap {
    public static int textureUnitRed;
    public static int textureUnitGreen;
    public static int textureUnitBlue;
    public static LightMap redMap;
    public static LightMap greenMap;
    public static LightMap blueMap;
    private static Minecraft mc;

    public final DynamicTexture texture;
    public final ResourceLocation location;
    public final int[] colors;
    public final int textureUnit;
    public LightMap(Minecraft minecraft, int textureUnit) {
        this.texture = new DynamicTexture(16, 16);
        this.location = minecraft.getTextureManager().getDynamicTextureLocation("lightMap", this.texture);
        this.colors = this.texture.getTextureData();
        this.textureUnit = textureUnit;
    }

    public LightMap(DynamicTexture texture, ResourceLocation location, int[] colors, int textureUnit) {
        this.texture = texture;
        this.location = location;
        this.colors = colors;
        this.textureUnit = textureUnit;
    }

    public static void init(Minecraft mc, LightMap vanilla) {
        redMap = vanilla;
        textureUnitRed = redMap.textureUnit;
        greenMap = new LightMap(mc, textureUnitGreen = GL13.GL_TEXTURE3);
        blueMap = new LightMap(mc, textureUnitBlue = GL13.GL_TEXTURE4);
        LightMap.mc = mc;
    }

    public static void enableReconfigureAll() {
        redMap.enableReconfigure();
        greenMap.enableReconfigure();
        blueMap.enableReconfigure();
    }

    public static void enableAll() {
        redMap.enable();
        greenMap.enable();
        blueMap.enable();
    }

    public static void disableAll() {
        redMap.disable();
        greenMap.disable();
        blueMap.disable();
    }

    public void enableReconfigure() {
        OpenGlHelper.setActiveTexture(textureUnit);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        float scale = 1 / 256F;
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        mc.getTextureManager().bindTexture(location);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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

    public static void updateLightMap(Minecraft mc, float torchFlickerX, float time)
    {
        WorldClient worldclient = mc.theWorld;

        if (worldclient != null) {
            for (int i = 0; i < 256; ++i) {
                float rawSunBrightness = worldclient.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float skyBlue = worldclient.provider.lightBrightnessTable[i / 16] * rawSunBrightness;
                float blockRed = worldclient.provider.lightBrightnessTable[i % 16] * (torchFlickerX * 0.1F + 1.5F);

                if (worldclient.lastLightningBolt > 0) {
                    skyBlue = worldclient.provider.lightBrightnessTable[i / 16];
                }

                float skyRed = skyBlue * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float skyGreen = skyBlue * (worldclient.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float blockGreen = blockRed * ((blockRed * 0.6F + 0.4F) * 0.6F + 0.4F);
                float blockBlue = blockRed * (blockRed * blockRed * 0.6F + 0.4F);
                float red = skyRed + blockRed;
                float green = skyGreen + blockGreen;
                float blue = skyBlue + blockBlue;
                red = red * 0.96F + 0.03F;
                green = green * 0.96F + 0.03F;
                blue = blue * 0.96F + 0.03F;

                if (worldclient.provider.dimensionId == 1) {
                    red = 0.22F + blockRed * 0.75F;
                    green = 0.28F + blockGreen * 0.75F;
                    blue = 0.25F + blockGreen * 0.75F;
                }

                red = clamp(red);
                green = clamp(green);
                blue = clamp(blue);

                float gamma = mc.gameSettings.gammaSetting;
                red = gammaCorrect(red, gamma);
                green = gammaCorrect(green, gamma);
                blue = gammaCorrect(blue, gamma);

                red = clamp(red);
                green = clamp(green);
                blue = clamp(blue);

                int iRed = (int)(red * 255.0F);
                int iGreen = (int)(green * 255.0F);
                int iBlue = (int)(blue * 255.0F);

                redMap.colors[i] = 0xFF00FFFF | (iRed << 16);
                greenMap.colors[i] = 0xFFFF00FF | (iGreen << 8);
                blueMap.colors[i] = 0xFFFFFF00 | iBlue;
            }

            redMap.texture.updateDynamicTexture();
            greenMap.texture.updateDynamicTexture();
            blueMap.texture.updateDynamicTexture();
        }
    }

    private static float clamp(float value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static float gammaCorrect(float color, float gammaSetting) {
        float colorPreGamma = 1 - color;
        colorPreGamma = 1 - colorPreGamma * colorPreGamma * colorPreGamma * colorPreGamma;
        color = color * (1 - gammaSetting) + colorPreGamma * gammaSetting;
        color = color * 0.96F + 0.03F;
        return color;
    }

}
