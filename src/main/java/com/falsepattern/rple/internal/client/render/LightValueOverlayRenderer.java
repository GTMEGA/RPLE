/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.lib.util.RenderUtil;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.Tags;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static com.falsepattern.rple.api.common.RPLEWorldUtil.*;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static net.minecraft.client.Minecraft.getMinecraft;

public final class LightValueOverlayRenderer {
    private static final float EPSILON = 0.004F;

    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;

    private static final float VALUE_WIDTH = 1F / (float) GRID_WIDTH;
    private static final float VALUE_HEIGHT = 1F / (float) GRID_HEIGHT;

    private static final Color RED_BLOCK_LIGHT_COLOR = new Color(0xBD0000);
    private static final Color GREEN_BLOCK_LIGHT_COLOR = new Color(0x19C400);
    private static final Color BLUE_BLOCK_LIGHT_COLOR = new Color(0x002DD0);

    private static final Color RED_SKY_LIGHT_COLOR = new Color(0xFF5353);
    private static final Color GREEN_SKY_LIGHT_COLOR = new Color(0x7DFF42);
    private static final Color BLUE_SKY_LIGHT_COLOR = new Color(0x00FFD9);

    private static final Color MIXED_BLOCK_LIGHT_COLOR = new Color(0xE0C43B);
    private static final Color MIXED_SKY_LIGHT_COLOR = new Color(0xACFFF8);
    private static final Color MIXED_ALL_LIGHT_COLOR = new Color(0xE094FF);

    private static final ResourceLocation LIGHT_VALUE_ATLAS_TEXTURE =
            new ResourceLocation(Tags.MOD_ID, "textures/overlays/light_value_atlas.png");

    private static final int HORIZONTAL_DRAW_RANGE = 30;
    private static final int VERTICAL_DRAW_RANGE = 20;

    public static void renderLightValueOverlay() {
        val minecraft = getMinecraft();
        val player = minecraft.thePlayer;
        val worldBase = minecraft.theWorld;
        val tess = Compat.tessellator();

        val basePosX = MathUtil.clamp((int) player.posX, -30_000_000, 30_000_000);
        val basePosY = MathUtil.clamp((int) player.posY, 0, 255);
        val basePosZ = MathUtil.clamp((int) player.posZ, -30_000_000, 30_000_000);

        val minPosX = Math.max(basePosX - HORIZONTAL_DRAW_RANGE, -30_000_000);
        val minPosY = Math.max(basePosY - VERTICAL_DRAW_RANGE, 0);
        val minPosZ = Math.max(basePosZ - HORIZONTAL_DRAW_RANGE, -30_000_000);

        val maxPosX = Math.min(basePosX + HORIZONTAL_DRAW_RANGE, 30_000_000);
        val maxPosY = Math.min(basePosY + VERTICAL_DRAW_RANGE, 255);
        val maxPosZ = Math.min(basePosZ + HORIZONTAL_DRAW_RANGE, 30_000_000);

        startDrawing(tess);
        for (int posY = minPosY; posY < maxPosY; posY++) {
            for (int posZ = minPosZ; posZ < maxPosZ; posZ++) {
                for (int posX = minPosX; posX < maxPosX; posX++) {
                    drawLightValueGridForPos(tess, worldBase, posX, posY, posZ);
                }
            }
        }
        finishDrawing(tess);
    }

    private static void startDrawing(Tessellator tess) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        getMinecraft().getTextureManager().bindTexture(LIGHT_VALUE_ATLAS_TEXTURE);

        GL11.glDepthMask(false);

        RenderUtil.setGLTranslationRelativeToPlayer();

        tess.startDrawing(GL11.GL_QUADS);
    }

    private static void finishDrawing(Tessellator tess) {
        tess.draw();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void drawLightValueGridForPos(Tessellator tess, World world, int posX, int posY, int posZ) {
        val blockMaterial = world.getBlock(posX, posY, posZ).getMaterial();
        if (blockMaterial.blocksMovement())
            return;
        val bottomBlockMaterial = world.getBlock(posX, posY - 1, posZ).getMaterial();
        if (!bottomBlockMaterial.blocksMovement())
            return;

        drawOutline(tess, posX, posY, posZ);

        val redBlockLight = getChannelBlockLightValue(world, RED_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 0, 0, RED_BLOCK_LIGHT_COLOR, redBlockLight);
        val greenBlockLight = getChannelBlockLightValue(world, GREEN_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 0, 1, GREEN_BLOCK_LIGHT_COLOR, greenBlockLight);
        val blueBlockLight = getChannelBlockLightValue(world, BLUE_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 0, 2, BLUE_BLOCK_LIGHT_COLOR, blueBlockLight);

        val redSkyLight = getChannelSkyLightValue(world, RED_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 1, 0, RED_SKY_LIGHT_COLOR, redSkyLight);
        val greenSkyLight = getChannelSkyLightValue(world, GREEN_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 1, 1, GREEN_SKY_LIGHT_COLOR, greenSkyLight);
        val blueSkyLight = getChannelSkyLightValue(world, BLUE_CHANNEL, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 1, 2, BLUE_SKY_LIGHT_COLOR, blueSkyLight);

        val greyscaleBlockLight = getGreyscaleBlockLightValue(world, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 2, 0, MIXED_BLOCK_LIGHT_COLOR, greyscaleBlockLight);
        val greyscaleSkyLight = getGreyscaleSkyLightValue(world, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 2, 1, MIXED_SKY_LIGHT_COLOR, greyscaleSkyLight);
        val greyscaleLightValue = getGreyscaleLightValue(world, posX, posY, posZ);
        drawNumber(tess, posX, posY, posZ, 2, 2, MIXED_ALL_LIGHT_COLOR, greyscaleLightValue);
    }

    private static void drawOutline(Tessellator tess, float posX, float posY, float posZ) {
        val minPosX = posX;
        val minPosY = posY - EPSILON;
        val minPosZ = posZ;

        val maxPosX = minPosX + 1F;
        val maxPosY = posY + EPSILON;
        val maxPosZ = minPosZ + 1F;

        val minTextureU = AtlasIcon.OUTLINE_ICON.minTextureU();
        val minTextureV = AtlasIcon.OUTLINE_ICON.minTextureV();

        val maxTextureU = AtlasIcon.OUTLINE_ICON.maxTextureU();
        val maxTextureV = AtlasIcon.OUTLINE_ICON.maxTextureV();

        tess.setColorRGBA(255, 255, 255, 255);

        tess.addVertexWithUV(minPosX, maxPosY, maxPosZ, minTextureU, maxTextureV);
        tess.addVertexWithUV(maxPosX, maxPosY, maxPosZ, maxTextureU, maxTextureV);
        tess.addVertexWithUV(maxPosX, maxPosY, minPosZ, maxTextureU, minTextureV);
        tess.addVertexWithUV(minPosX, maxPosY, minPosZ, minTextureU, minTextureV);

        tess.addVertexWithUV(minPosX, minPosY, minPosZ, maxTextureU, minTextureV);
        tess.addVertexWithUV(maxPosX, minPosY, minPosZ, minTextureU, minTextureV);
        tess.addVertexWithUV(maxPosX, minPosY, maxPosZ, minTextureU, maxTextureV);
        tess.addVertexWithUV(minPosX, minPosY, maxPosZ, maxTextureU, maxTextureV);
    }

    private static void drawNumber(Tessellator tess,
                                   float posX,
                                   float posY,
                                   float posZ,
                                   int gridX,
                                   int gridY,
                                   Color color,
                                   int value) {
        gridX = MathUtil.clamp(gridX, 0, GRID_WIDTH - 1);
        gridY = MathUtil.clamp(gridY, 0, GRID_HEIGHT - 1);

        val posXOffset = (float) gridX * VALUE_WIDTH;
        val posZOffset = (float) gridY * VALUE_HEIGHT;

        val minPosX = posX + posXOffset;
        val minPosY = posY - EPSILON;
        val minPosZ = posZ + posZOffset;

        val maxPosX = minPosX + VALUE_WIDTH;
        val maxPosY = posY + EPSILON;
        val maxPosZ = minPosZ + VALUE_HEIGHT;

        val valueIcon = AtlasIcon.forValue(value);

        val minTextureU = valueIcon.minTextureU();
        val minTextureV = valueIcon.minTextureV();

        val maxTextureU = valueIcon.maxTextureU();
        val maxTextureV = valueIcon.maxTextureV();

        tess.setColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        tess.addVertexWithUV(minPosX, maxPosY, maxPosZ, minTextureU, maxTextureV);
        tess.addVertexWithUV(maxPosX, maxPosY, maxPosZ, maxTextureU, maxTextureV);
        tess.addVertexWithUV(maxPosX, maxPosY, minPosZ, maxTextureU, minTextureV);
        tess.addVertexWithUV(minPosX, maxPosY, minPosZ, minTextureU, minTextureV);

        tess.addVertexWithUV(minPosX, minPosY, minPosZ, maxTextureU, minTextureV);
        tess.addVertexWithUV(maxPosX, minPosY, minPosZ, minTextureU, minTextureV);
        tess.addVertexWithUV(maxPosX, minPosY, maxPosZ, minTextureU, maxTextureV);
        tess.addVertexWithUV(minPosX, minPosY, maxPosZ, maxTextureU, maxTextureV);
    }

    @Getter
    @Accessors(fluent = true, chain = false)
    private enum AtlasIcon {
        VALUE_0_ICON(0, 0),
        VALUE_1_ICON(1, 0),
        VALUE_2_ICON(2, 0),
        VALUE_3_ICON(3, 0),
        VALUE_4_ICON(0, 1),
        VALUE_5_ICON(1, 1),
        VALUE_6_ICON(2, 1),
        VALUE_7_ICON(3, 1),
        VALUE_8_ICON(0, 2),
        VALUE_9_ICON(1, 2),
        VALUE_10_ICON(2, 2),
        VALUE_11_ICON(3, 2),
        VALUE_12_ICON(0, 3),
        VALUE_13_ICON(1, 3),
        VALUE_14_ICON(2, 3),
        VALUE_15_ICON(3, 3),
        OUTLINE_ICON(4, 0),

        ;

        private static final int ATLAS_WIDTH = 5;
        private static final int ATLAS_HEIGHT = 4;

        private static final float ICON_WIDTH = 1F / (float) ATLAS_WIDTH;
        private static final float ICON_HEIGHT = 1F / (float) ATLAS_HEIGHT;

        private static final int MIN_VALUE = 0;
        private static final int MAX_VALUE = 15;

        private final float minTextureU;
        private final float minTextureV;

        private final float maxTextureU;
        private final float maxTextureV;

        AtlasIcon(int posX, int posY) {
            this.minTextureU = posX * ICON_WIDTH;
            this.minTextureV = posY * ICON_HEIGHT;

            this.maxTextureU = minTextureU + ICON_WIDTH;
            this.maxTextureV = minTextureV + ICON_HEIGHT;
        }

        public static AtlasIcon forValue(int value) {
            value = MathUtil.clamp(value, MIN_VALUE, MAX_VALUE);
            return values()[value];
        }
    }
}
