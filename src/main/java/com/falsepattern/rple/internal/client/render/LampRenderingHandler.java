/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.falsetweaks.Compat;
import com.falsepattern.falsetweaks.api.triangulator.ToggleableTessellator;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.blocks.Lamp;
import com.falsepattern.rple.internal.color.BrightnessUtil;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class LampRenderingHandler implements ISimpleBlockRenderingHandler {
    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
    public static final float OFFSET = 0.05F;
    private static final boolean[][][] NULL = new boolean[3][3][3];

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        val tessellator = Compat.tessellator();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);
        val icon = block.getIcon(0, meta);
        val glowing = Lamp.isGlowing(meta);
        if (glowing) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        }
        drawCubeInventory(tessellator, renderer, block, 0, 0, 0, icon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        if (glowing) {
            val lamp = (Lamp)block;
            val r = lamp.getColoredLightValue(null, meta, LightConstants.COLOR_CHANNEL_RED, 0, 0, 0) * 17;
            val g = lamp.getColoredLightValue(null, meta, LightConstants.COLOR_CHANNEL_GREEN, 0, 0, 0) * 17;
            val b = lamp.getColoredLightValue(null, meta, LightConstants.COLOR_CHANNEL_BLUE, 0, 0, 0) * 17;
            tessellator.setBrightness(BrightnessUtil.lightLevelsToBrightness(15, 15));
            tessellator.setColorRGBA(r, g, b, 128);
            drawGlowCube(tessellator, 0, 0, 0, NULL, lamp.getGlowIcon());
        }
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        val lamp = (Lamp) block;
        val tessellator = Compat.tessellator();
        switch (((ToggleableTessellator) tessellator).pass()) {
            case 0:
                return renderer.renderStandardBlock(lamp, x, y, z);
            case 1:
                val meta = world.getBlockMetadata(x, y, z);
                val powered = (meta & Lamp.POWERED_BIT) != 0;
                val inverted = (meta & Lamp.INVERTED_BIT) != 0;
                if (powered != inverted) {
                    val neighbors = new boolean[3][3][3];
                    for (int Z = 0; Z <= 2; Z++) {
                        for (int Y = 0; Y <= 2; Y++) {
                            for (int X = 0; X <= 2; X++) {
                                if (X == 1 && Y == 1 && Z == 1) {
                                    continue;
                                }
                                neighbors[X][Y][Z] = (Y - 1 + y) < 256 && (Y - 1 + y) > 0 && isBlockGlowingLamp(world, X - 1 + x, Y - 1 + y, Z - 1 + z);
                            }
                        }
                    }
                    val r = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z) * 17;
                    val g = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z) * 17;
                    val b = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z) * 17;
                    tessellator.setBrightness(BrightnessUtil.lightLevelsToBrightness(15, 15));
                    tessellator.setColorOpaque(r, g, b);
                    drawGlowCube(tessellator, x, y, z, neighbors, lamp.getGlowIcon());
                    return true;
                }
            default:
                return false;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RENDER_ID;
    }

    private static boolean isBlockGlowingLamp(IBlockAccess world, int x, int y, int z) {
        val block = world.getBlock(x, y, z);
        if (block instanceof Lamp) {
            val meta = world.getBlockMetadata(x, y, z);
            val powered = (meta & Lamp.POWERED_BIT) != 0;
            val inverted = (meta & Lamp.INVERTED_BIT) != 0;
            return powered != inverted;
        }
        return false;
    }

    private static void drawCubeInventory(Tessellator tessellator, RenderBlocks renderer, Block block, double x, double y, double z, IIcon texture) {
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, x, y, z, texture);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, x, y, z, texture);
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, x, y, z, texture);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, x, y, z, texture);
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, x, y, z, texture);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, x, y, z, texture);
    }

    private static void drawGlowCube(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        if (!neighbors[2][1][1])
            drawXPos(tessellator, x, y, z, neighbors, icon);
        if (!neighbors[0][1][1])
            drawXNeg(tessellator, x, y, z, neighbors, icon);
        if (!neighbors[1][2][1])
            drawYPos(tessellator, x, y, z, neighbors, icon);
        if (!neighbors[1][0][1])
            drawYNeg(tessellator, x, y, z, neighbors, icon);
        if (!neighbors[1][1][2])
            drawZPos(tessellator, x, y, z, neighbors, icon);
        if (!neighbors[1][1][0])
            drawZNeg(tessellator, x, y, z, neighbors, icon);
    }

    //Face drawing

    private static void drawXPos(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[1 + forward][y1][x1]);
        for (val segment: segments) {
            drawXPos(tessellator, y + segment[1], y + segment[3], z + segment[0], z + segment[2], x + 1 + OFFSET, icon);
        }
    }

    private static void drawXNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[1 - forward][y1][x1]);
        for (val segment: segments) {
            drawXNeg(tessellator, y + segment[1], y + segment[3], z + segment[0], z + segment[2], x - OFFSET, icon);
        }
    }

    private static void drawYPos(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][1 + forward][x1]);
        for (val segment: segments) {
            drawYPos(tessellator, x + segment[1], x + segment[3], z + segment[0], z + segment[2], y + 1 + OFFSET, icon);
        }
    }

    private static void drawYNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][1 - forward][x1]);
        for (val segment: segments) {
            drawYNeg(tessellator, x + segment[1], x + segment[3], z + segment[0], z + segment[2], y - OFFSET, icon);
        }
    }

    private static void drawZPos(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][x1][1 + forward]);
        for (val segment: segments) {
            drawZPos(tessellator, x + segment[1], x + segment[3], y + segment[0], y + segment[2], z + 1 + OFFSET, icon);
        }
    }

    private static void drawZNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val segments = genSegments(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][x1][1 - forward]);
        for (val segment: segments) {
            drawZNeg(tessellator, x + segment[1], x + segment[3], y + segment[0], y + segment[2], z - OFFSET, icon);
        }
    }

    //Face toggling

    private interface Sampler {
        boolean sample(boolean[][][] neighbors, int x, int y, int forward);
    }
    private static float[][] genSegments(boolean[][][] neighbors, Sampler sampler) {
        var xMin = new float[]{-OFFSET, -OFFSET, OFFSET, 1 + OFFSET};
        var center = new float[]{OFFSET, -OFFSET, 1 - OFFSET, 1 + OFFSET};
        var xMax = new float[]{1 - OFFSET, -OFFSET, 1 + OFFSET, 1 + OFFSET};
        //planar edges
        if (sampler.sample(neighbors, 1, 2, 0)) {
            xMin[3] = 1;
            center[3] = 1;
            xMax[3] = 1;
        }
        if (sampler.sample(neighbors, 1, 0, 0)) {
            xMin[1] = 0;
            center[1] = 0;
            xMax[1] = 0;
        }
        if (sampler.sample(neighbors, 2, 1, 0)) {
            xMax[2] = 1;
        }
        if (sampler.sample(neighbors, 0, 1, 0)) {
            xMin[0] = 0;
        }

        //planar corners
        if (sampler.sample(neighbors, 0, 0, 0)) {
            xMin[1] = 0;
        }
        if (sampler.sample(neighbors, 2, 0, 0)) {
            xMax[1] = 0;
        }
        if (sampler.sample(neighbors, 0, 2, 0)) {
            xMin[3] = 1;
        }
        if (sampler.sample(neighbors, 2, 2, 0)) {
            xMax[3] = 1;
        }

        //front corners
        if (sampler.sample(neighbors, 0, 0, 1)) {
            xMin[1] = OFFSET;
        }
        if (sampler.sample(neighbors, 2, 0, 1)) {
            xMax[1] = OFFSET;
        }
        if (sampler.sample(neighbors, 0, 2, 1)) {
            xMin[3] = 1 - OFFSET;
        }
        if (sampler.sample(neighbors, 2, 2, 1)) {
            xMax[3] = 1 - OFFSET;
        }

        //front edges
        if (sampler.sample(neighbors, 1, 0, 1)) {
            xMin[1] = OFFSET;
            center[1] = OFFSET;
            xMax[1] = OFFSET;
        }
        if (sampler.sample(neighbors, 1, 2, 1)) {
            xMin[3] = 1 - OFFSET;
            center[3] = 1 - OFFSET;
            xMax[3] = 1 - OFFSET;
        }
        if (sampler.sample(neighbors, 0, 1, 1)) {
            xMin = null;
        }
        if (sampler.sample(neighbors, 2, 1, 1)) {
            xMax = null;
        }

        return merge(xMin, center, xMax);
    }

    private static float[][] merge(float[] xMin, float[] center, float[] xMax) {
        if (xMin != null && xMin[1] == center[1] && xMin[3] == center[3]) {
            center[0] = xMin[0];
            xMin = null;
        }
        if (xMax != null && center[1] == xMax[1] && center[3] == xMax[3]) {
            center[2] = xMax[2];
            xMax = null;
        }
        val result = new float[1 + (xMin == null ? 0 : 1) + (xMax == null ? 0 : 1)][];
        int i = 0;
        if (xMin != null)
            result[i++] = xMin;
        result[i++] = center;
        if (xMax != null)
            result[i] = xMax;
        return result;
    }

    //Quad drawing

    private static void drawXPos(Tessellator tessellator, double minY, double maxY, double minZ, double maxZ, double x, IIcon icon) {
        tessellator.addVertexWithUV(x, maxY, minZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, maxY, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, minY, maxZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, minY, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawXNeg(Tessellator tessellator, double minY, double maxY, double minZ, double maxZ, double x, IIcon icon) {
        tessellator.addVertexWithUV(x, maxY, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, maxY, minZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, minY, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, minY, maxZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawYPos(Tessellator tessellator, double minX, double maxX, double minZ, double maxZ, double y, IIcon icon) {
        tessellator.addVertexWithUV(minX, y, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, y, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, y, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(minX, y, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawYNeg(Tessellator tessellator, double minX, double maxX, double minZ, double maxZ, double y, IIcon icon) {
        tessellator.addVertexWithUV(maxX, y, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, y, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, y, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(maxX, y, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawZPos(Tessellator tessellator, double minX, double maxX, double minY, double maxY, double z, IIcon icon) {
        tessellator.addVertexWithUV(maxX, maxY, z, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, maxY, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, minY, z, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(maxX, minY, z, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawZNeg(Tessellator tessellator, double minX, double maxX, double minY, double maxY, double z, IIcon icon) {
        tessellator.addVertexWithUV(minX, maxY, z, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, maxY, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, minY, z, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(minX, minY, z, icon.getMaxU(), icon.getMaxV());
    }
}
