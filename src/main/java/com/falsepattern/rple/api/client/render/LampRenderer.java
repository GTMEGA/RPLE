/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.api.client.render;

import com.falsepattern.falsetweaks.api.triangulator.ToggleableTessellator;
import com.falsepattern.rple.api.common.lamp.LampBlock;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class LampRenderer implements ISimpleBlockRenderingHandler {
    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

    private static final float OFFSET = 0.05F;

    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        val tessellator = Compat.tessellator();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);

        val glowing = LampBlock.isGlowing(meta);
        if (glowing)
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);

        val icon = block.getIcon(0, meta);
        drawCubeInventory(tessellator, renderer, block, 0, 0, 0, icon);
        tessellator.draw();

        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.startDrawingQuads();

        if (glowing) {
            val lamp = (LampBlock) block;
            val color = lamp.getColor();
            val r = color.red() * 17;
            val g = color.green() * 17;
            val b = color.blue() * 17;
            tessellator.setBrightness(TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(15, 15));
            tessellator.setColorRGBA(r, g, b, 128);
            drawGlowCube(tessellator, 0, 0, 0, 0, lamp.getGlowIcon());
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world,
                                    int x,
                                    int y,
                                    int z,
                                    Block block,
                                    int modelId,
                                    RenderBlocks renderer) {
        val lamp = (LampBlock) block;
        val tessellator = Compat.tessellator();

        val renderPass = ((ToggleableTessellator) tessellator).pass();
        if (renderPass == 0)
            return renderer.renderStandardBlock(lamp, x, y, z);

        if (renderPass != 1)
            return false;

        val meta = world.getBlockMetadata(x, y, z);
        val powered = (meta & LampBlock.POWERED_BIT) != 0;
        val inverted = (meta & LampBlock.INVERTED_BIT) != 0;
        if (powered == inverted)
            return false;

        int neighbors = 0;
        for (int Z = 0; Z <= 2; Z++) {
            for (int Y = 0; Y <= 2; Y++) {
                for (int X = 0; X <= 2; X++) {
                    if (X == 1 && Y == 1 && Z == 1)
                        continue;
                    int BX = X - 1 + x;
                    int BY = Y - 1 + y;
                    int BZ = Z - 1 + z;
                    if (BY < 256 && BY >= 0 && isBlockGlowingLamp(world, BX, BY, BZ)) {
                        neighbors = setNeighbor(neighbors, X, Y, Z);
                    }
                }
            }
        }

        val color = lamp.getColor();
        val r = color.red() * 17;
        val g = color.green() * 17;
        val b = color.blue() * 17;

        tessellator.setBrightness(TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(15, 15));
        tessellator.setColorOpaque(r, g, b);
        drawGlowCube(tessellator, x, y, z, neighbors, lamp.getGlowIcon());
        return true;
    }

    private static int toShift(int X, int Y, int Z) {
        return X * 9 + Y * 3 + Z;
    }

    private static int toBitMask(int X, int Y, int Z) {
        return 1 << toShift(X, Y, Z);
    }

    private static int setNeighbor(int neighbors, int X, int Y, int Z) {
        return neighbors | toBitMask(X, Y, Z);
    }

    private static boolean getNeighbor(int neighbors, int X, int Y, int Z) {
        return (neighbors & toBitMask(X, Y, Z)) != 0;
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
        if (!(block instanceof LampBlock))
            return false;

        val meta = world.getBlockMetadata(x, y, z);
        val powered = (meta & LampBlock.POWERED_BIT) != 0;
        val inverted = (meta & LampBlock.INVERTED_BIT) != 0;
        return powered != inverted;
    }

    private static void drawCubeInventory(Tessellator tessellator, RenderBlocks renderer, Block block, double x,
                                          double y, double z, IIcon texture) {
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

    private static final int DGC_MASK_X_POS = toBitMask(2, 1, 1);
    private static final int DGC_MASK_X_NEG = toBitMask(0, 1, 1);
    private static final int DGC_MASK_Y_POS = toBitMask(1, 2, 1);
    private static final int DGC_MASK_Y_NEG = toBitMask(1, 0, 1);
    private static final int DGC_MASK_Z_POS = toBitMask(1, 1, 2);
    private static final int DGC_MASK_Z_NEG = toBitMask(1, 1, 0);
    private static final int DGC_MASK_ALL = DGC_MASK_X_POS | DGC_MASK_X_NEG | DGC_MASK_Y_POS | DGC_MASK_Y_NEG | DGC_MASK_Z_POS | DGC_MASK_Z_NEG;
    private static void drawGlowCube(Tessellator tessellator, double x, double y, double z,
                                     int neighbors, IIcon icon) {
        if ((neighbors & DGC_MASK_ALL) == DGC_MASK_ALL)
            return;
        val sBuf = S_BUF.get();
        if ((neighbors & DGC_MASK_X_POS) == 0)
            drawXPos(sBuf, tessellator, x, y, z, neighbors, icon);
        if ((neighbors & DGC_MASK_X_NEG) == 0)
            drawXNeg(sBuf, tessellator, x, y, z, neighbors, icon);
        if ((neighbors & DGC_MASK_Y_POS) == 0)
            drawYPos(sBuf, tessellator, x, y, z, neighbors, icon);
        if ((neighbors & DGC_MASK_Y_NEG) == 0)
            drawYNeg(sBuf, tessellator, x, y, z, neighbors, icon);
        if ((neighbors & DGC_MASK_Z_POS) == 0)
            drawZPos(sBuf, tessellator, x, y, z, neighbors, icon);
        if ((neighbors & DGC_MASK_Z_NEG) == 0)
            drawZNeg(sBuf, tessellator, x, y, z, neighbors, icon);
    }

    // region Face drawing
    private static final Sampler XPosSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, 1 + forward, y, x);
    private static final Sampler XNegSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, 1 - forward, y, x);
    private static final Sampler YPosSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, y, 1 + forward, x);
    private static final Sampler YNegSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, y, 1 - forward, x);
    private static final Sampler ZPosSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, y, x, 1 + forward);
    private static final Sampler ZNegSampler = (neighbors, x, y, forward) -> getNeighbor(neighbors, y, x, 1 - forward);

    private static void drawXPos(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, XPosSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawXPos(tessellator, y + segment[1], y + segment[3], z + segment[0], z + segment[2], x + 1 + OFFSET, icon);
        }
    }

    private static void drawXNeg(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, XNegSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawXNeg(tessellator, y + segment[1], y + segment[3], z + segment[0], z + segment[2], x - OFFSET, icon);
        }
    }

    private static void drawYPos(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, YPosSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawYPos(tessellator, x + segment[1], x + segment[3], z + segment[0], z + segment[2], y + 1 + OFFSET, icon);
        }
    }

    private static void drawYNeg(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, YNegSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawYNeg(tessellator, x + segment[1], x + segment[3], z + segment[0], z + segment[2], y - OFFSET, icon);
        }
    }

    private static void drawZPos(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, ZPosSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawZPos(tessellator, x + segment[1], x + segment[3], y + segment[0], y + segment[2], z + 1 + OFFSET, icon);
        }
    }

    private static void drawZNeg(SegmentBuffer sBuf, Tessellator tessellator, double x, double y, double z,
                                 int neighbors, IIcon icon) {
        val segments = sBuf.result;
        val sCount = genSegments(sBuf, neighbors, ZNegSampler);
        for (int i = 0; i < sCount; i++) {
            val segment = segments[i];
            drawZNeg(tessellator, x + segment[1], x + segment[3], y + segment[0], y + segment[2], z - OFFSET, icon);
        }
    }
    // endregion

    // region Face Toggling
    private interface Sampler {
        boolean sample(int neighbors, int x, int y, int forward);
    }

    private static class SegmentBuffer {
        private static final float[] DefaultCenter = new float[]{OFFSET, -OFFSET, 1 - OFFSET, 1 + OFFSET};
        private static final float[] DefaultXMin = new float[]{-OFFSET, -OFFSET, OFFSET, 1 + OFFSET};
        private static final float[] DefaultXMax = new float[]{1 - OFFSET, -OFFSET, 1 + OFFSET, 1 + OFFSET};
        public final float[] center = new float[4];
        public final float[] xMin = new float[4];
        public final float[] xMax = new float[4];
        public final float[][] result = new float[3][];

        public void init() {
            System.arraycopy(DefaultCenter, 0, center, 0, 4);
            System.arraycopy(DefaultXMin, 0, xMin, 0, 4);
            System.arraycopy(DefaultXMax, 0, xMax, 0, 4);
        }
    }

    private static final ThreadLocal<SegmentBuffer> S_BUF = ThreadLocal.withInitial(SegmentBuffer::new);

    private static int genSegments(SegmentBuffer sBuf, int neighbors, Sampler sampler) {
        sBuf.init();
        val center = sBuf.center;
        var xMin = sBuf.xMin;
        var xMax = sBuf.xMax;

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

        return merge(sBuf, xMin, center, xMax);
    }

    private static int merge(SegmentBuffer sBuf, float[] xMin, float[] center, float[] xMax) {
        if (xMin != null && xMin[1] == center[1] && xMin[3] == center[3]) {
            center[0] = xMin[0];
            xMin = null;
        }
        if (xMax != null && center[1] == xMax[1] && center[3] == xMax[3]) {
            center[2] = xMax[2];
            xMax = null;
        }

        val result = sBuf.result;

        var i = 0;
        if (xMin != null) {
            result[i] = xMin;
            i++;
        }
        result[i] = center;
        i++;
        if (xMax != null) {
            result[i] = xMax;
            i++;
        }

        return i;
    }
    // endregion

    // region Quad drawing
    private static void drawXPos(Tessellator tessellator, double minY, double maxY, double minZ, double maxZ,
                                 double x, IIcon icon) {
        tessellator.addVertexWithUV(x, maxY, minZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, maxY, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, minY, maxZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, minY, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawXNeg(Tessellator tessellator, double minY, double maxY, double minZ, double maxZ,
                                 double x, IIcon icon) {
        tessellator.addVertexWithUV(x, maxY, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, maxY, minZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, minY, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, minY, maxZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawYPos(Tessellator tessellator, double minX, double maxX, double minZ, double maxZ,
                                 double y, IIcon icon) {
        tessellator.addVertexWithUV(minX, y, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, y, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, y, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(minX, y, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawYNeg(Tessellator tessellator, double minX, double maxX, double minZ, double maxZ,
                                 double y, IIcon icon) {
        tessellator.addVertexWithUV(maxX, y, maxZ, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, y, maxZ, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, y, minZ, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(maxX, y, minZ, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawZPos(Tessellator tessellator, double minX, double maxX, double minY, double maxY,
                                 double z, IIcon icon) {
        tessellator.addVertexWithUV(maxX, maxY, z, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, maxY, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(minX, minY, z, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(maxX, minY, z, icon.getMaxU(), icon.getMaxV());
    }

    private static void drawZNeg(Tessellator tessellator, double minX, double maxX, double minY, double maxY,
                                 double z, IIcon icon) {
        tessellator.addVertexWithUV(minX, maxY, z, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, maxY, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(maxX, minY, z, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(minX, minY, z, icon.getMaxU(), icon.getMaxV());
    }
    // endregion
}
