/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.falsetweaks.Compat;
import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.lib.util.RenderUtil;
import com.falsepattern.rple.internal.Tags;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

import static net.minecraft.client.Minecraft.getMinecraft;

public final class LightValueOverlayRenderer {
    private static final float EPSILON = 0.004F;

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 15;

    private static final int ATLAS_WIDTH = 4;
    private static final int ATLAS_HEIGHT = 4;

    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;

    private static final float ICON_WIDTH = 1F / (float) ATLAS_WIDTH;
    private static final float ICON_HEIGHT = 1F / (float) ATLAS_HEIGHT;

    private static final float VALUE_WIDTH = 1F / (float) GRID_WIDTH;
    private static final float VALUE_HEIGHT = 1F / (float) GRID_HEIGHT;

    private static final Color RED_BLOCK_LIGHT_COLOR = new Color(0xBD0000);
    private static final Color GREEN_BLOCK_LIGHT_COLOR = new Color(0x19C400);
    private static final Color BLUE_BLOCK_LIGHT_COLOR = new Color(0x002DD0);

    private static final Color RED_SKY_LIGHT_COLOR = new Color(0xFF5353);
    private static final Color GREEN_SKY_LIGHT_COLOR = new Color(0x7DFF42);
    private static final Color BLUE_SKY_LIGHT_COLOR = new Color(0x00FFD9);

    private static final Color MIXED_BLOCK_LIGHT_COLOR = new Color(0xACFFF8);
    private static final Color MIXED_SKY_LIGHT_COLOR = new Color(0xE0C43B);
    private static final Color MIXED_ALL_LIGHT_COLOR = new Color(0xFEF9D6);

    private static final ResourceLocation LIGHT_VALUE_ATLAS_TEXTURE =
            new ResourceLocation(Tags.MOD_ID, "textures/overlays/light_value_atlas.png");

    public static void renderLightValueOverlay() {
        val minecraft = getMinecraft();
        val player = minecraft.thePlayer;
        val textureManager = minecraft.getTextureManager();
        val tess = Compat.tessellator();

        val basePosX = MathUtil.clamp((int) player.posX, -30_000_000, 30_000_000);
        val basePosY = MathUtil.clamp((int) player.posY, 0, 255);
        val basePosZ = MathUtil.clamp((int) player.posZ, -30_000_000, 30_000_000);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glColor4f(1F, 1F, 1F, 1F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        textureManager.bindTexture(LIGHT_VALUE_ATLAS_TEXTURE);

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.setGLTranslationRelativeToPlayer();

        tess.startDrawing(GL11.GL_QUADS);
        drawNumber(tess, basePosX, basePosY, basePosZ, 0, 0, RED_BLOCK_LIGHT_COLOR, 0);
        drawNumber(tess, basePosX, basePosY, basePosZ, 0, 1, GREEN_BLOCK_LIGHT_COLOR, 1);
        drawNumber(tess, basePosX, basePosY, basePosZ, 0, 2, BLUE_BLOCK_LIGHT_COLOR, 2);

        drawNumber(tess, basePosX, basePosY, basePosZ, 1, 0, RED_SKY_LIGHT_COLOR, 3);
        drawNumber(tess, basePosX, basePosY, basePosZ, 1, 1, GREEN_SKY_LIGHT_COLOR, 4);
        drawNumber(tess, basePosX, basePosY, basePosZ, 1, 2, BLUE_SKY_LIGHT_COLOR, 5);

        drawNumber(tess, basePosX, basePosY, basePosZ, 2, 0, MIXED_BLOCK_LIGHT_COLOR, 6);
        drawNumber(tess, basePosX, basePosY, basePosZ, 2, 1, MIXED_SKY_LIGHT_COLOR, 7);
        drawNumber(tess, basePosX, basePosY, basePosZ, 2, 2, MIXED_ALL_LIGHT_COLOR, 8);

        tess.draw();

        GL11.glPopMatrix();
        GL11.glPopAttrib();
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
        value = MathUtil.clamp(value, MIN_VALUE, MAX_VALUE);

        val posXOffset = (float) gridX * VALUE_WIDTH;
        val posZOffset = (float) gridY * VALUE_HEIGHT;

        val minPosX = posX + posXOffset;
        val minPosY = posY - EPSILON;
        val minPosZ = posZ + posZOffset;

        val maxPosX = minPosX + VALUE_WIDTH;
        val maxPosY = posY + EPSILON;
        val maxPosZ = minPosZ + VALUE_HEIGHT;

        val minTextureU = (float) (value % ATLAS_WIDTH) * ICON_WIDTH;
        val minTextureV = (float) (value / ATLAS_HEIGHT) * ICON_HEIGHT;

        val maxTextureU = minTextureU + ICON_WIDTH;
        val maxTextureV = minTextureV + ICON_HEIGHT;

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
}
