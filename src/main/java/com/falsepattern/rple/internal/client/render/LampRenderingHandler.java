/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.falsetweaks.Compat;
import com.falsepattern.falsetweaks.api.triangulator.ToggleableTessellator;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.blocks.Lamp;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.falsepattern.rple.internal.color.BrightnessUtil;
import lombok.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.*;

@SideOnly(Side.CLIENT)
public class LampRenderingHandler implements ISimpleBlockRenderingHandler {
    public static final int RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
    public static final float OFFSET = 0.05F;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        val tessellator = Compat.tessellator();
        tessellator.startDrawingQuads();
        drawCubeInventory(tessellator, renderer, block, 0, 0, 0, block.getIcon(0, 0));
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
                if (lamp.powered) {
                    val neighbors = new boolean[3][3][3];
                    for (int Z = 0; Z <= 2; Z++) {
                        for (int Y = 0; Y <= 2; Y++) {
                            for (int X = 0; X <= 2; X++) {
                                if (X == 1 && Y == 1 && Z == 1) {
                                    continue;
                                }
                                neighbors[X][Y][Z] = (Y - 1 + y) >= 255 || (Y - 1 + y) <= 0 || renderSide(world.getBlock(X - 1 + x, Y - 1 + y, Z - 1 + z));
                            }
                        }
                    }
                    val meta = world.getBlockMetadata(x, y, z);
                    val r = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z) * 17;
                    val g = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z) * 17;
                    val b = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z) * 17;
                    tessellator.setBrightness(BrightnessUtil.lightLevelsToBrightness(15, 15));
                    tessellator.setColorRGBA(r, g, b, 128);
                    drawCube(tessellator, x, y, z, neighbors, lamp.getGlowIcon());
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

    private static boolean renderSide(Block block) {
        return block instanceof Lamp && ((Lamp)block).powered;
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

    private static void drawCube(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
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
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[1 + forward][y1][x1]);

        for (int Y = 0; Y < 5; Y++) {
            for (int Z = 0; Z < 5; Z++) {
                if (faces[Y][Z]) {
                    double minY = remapMin(Y, y);
                    double maxY = remapMax(Y, y);
                    double minZ = remapMin(Z, z);
                    double maxZ = remapMax(Z, z);
                    drawXPos(tessellator, minY, maxY, minZ, maxZ, x + 1 + OFFSET, icon);
                }
            }
        }
    }

    private static void drawXNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[1 - forward][y1][x1]);
        for (int Y = 0; Y < 5; Y++) {
            for (int Z = 0; Z < 5; Z++) {
                if (faces[Y][Z]) {
                    double minY = remapMin(Y, y);
                    double maxY = remapMax(Y, y);
                    double minZ = remapMin(Z, z);
                    double maxZ = remapMax(Z, z);
                    drawXNeg(tessellator, minY, maxY, minZ, maxZ, x - OFFSET, icon);
                }
            }
        }
    }

    private static void drawYPos(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][forward + 1][x1]);
        for (int X = 0; X < 5; X++) {
            for (int Z = 0; Z < 5; Z++) {
                if (faces[X][Z]) {
                    double minX = remapMin(X, x);
                    double maxX = remapMax(X, x);
                    double minZ = remapMin(Z, z);
                    double maxZ = remapMax(Z, z);
                    drawYPos(tessellator, minX, maxX, minZ, maxZ, y + 1 + OFFSET, icon);
                }
            }
        }
    }

    private static void drawYNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][1 - forward][x1]);
        for (int X = 0; X < 5; X++) {
            for (int Z = 0; Z < 5; Z++) {
                if (faces[X][Z]) {
                    double minX = remapMin(X, x);
                    double maxX = remapMax(X, x);
                    double minZ = remapMin(Z, z);
                    double maxZ = remapMax(Z, z);
                    drawYNeg(tessellator, minX, maxX, minZ, maxZ, y - OFFSET, icon);
                }
            }
        }
    }

    private static void drawZPos(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][x1][1 + forward]);
        for (int X = 0; X < 5; X++) {
            for (int Y = 0; Y < 5; Y++) {
                if (faces[X][Y]) {
                    double minX = remapMin(X, x);
                    double maxX = remapMax(X, x);
                    double minY = remapMin(Y, y);
                    double maxY = remapMax(Y, y);
                    drawZPos(tessellator, minX, maxX, minY, maxY, z + 1 + OFFSET, icon);
                }
            }
        }
    }

    private static void drawZNeg(Tessellator tessellator, double x, double y, double z, boolean[][][] neighbors, IIcon icon) {
        val faces = genFaceMap(neighbors, (neighbors1, x1, y1, forward) -> neighbors1[y1][x1][1 - forward]);
        for (int X = 0; X < 5; X++) {
            for (int Y = 0; Y < 5; Y++) {
                if (faces[X][Y]) {
                    double minX = remapMin(X, x);
                    double maxX = remapMax(X, x);
                    double minY = remapMin(Y, y);
                    double maxY = remapMax(Y, y);
                    drawZNeg(tessellator, minX, maxX, minY, maxY, z - OFFSET, icon);
                }
            }
        }
    }

    private static double remapMin(int index, double core) {
        switch (index) {
            case 0:
                return core - OFFSET;
            case 1:
                return core;
            case 2:
                return core + OFFSET;
            case 3:
                return core + 1 - OFFSET;
            default:
                return core + 1;
        }
    }

    private static double remapMax(int index, double core) {
        switch (index) {
            case 0:
                return core;
            case 1:
                return core + OFFSET;
            case 2:
                return core + 1 - OFFSET;
            case 3:
                return core + 1;
            default:
                return core + 1 + OFFSET;
        }
    }

    //Face toggling

    private interface Sampler {
        boolean sample(boolean[][][] neighbors, int x, int y, int forward);
    }

    private static boolean[][] genFaceMap(boolean[][][] neighbors, Sampler sampler) {
        val faces = newArr();

        //planar edges
        if (sampler.sample(neighbors, 1, 2, 0))
            turnOffRegion(faces, 0, 4, 5, 1);
        if (sampler.sample(neighbors, 1, 0, 0))
            turnOffRegion(faces, 0, 0, 5, 1);
        if (sampler.sample(neighbors, 2, 1, 0))
            turnOffRegion(faces, 4, 0, 1, 5);
        if (sampler.sample(neighbors, 0, 1, 0))
            turnOffRegion(faces, 0, 0, 1, 5);

        //planar corners
        if (sampler.sample(neighbors, 0, 0, 0))
            turnOffRegion(faces, 0, 0, 2, 1);
        if (sampler.sample(neighbors, 2, 0, 0))
            turnOffRegion(faces, 3, 0, 2, 1);
        if (sampler.sample(neighbors, 0, 2, 0))
            turnOffRegion(faces, 0, 4, 2, 1);
        if (sampler.sample(neighbors, 2, 2, 0))
            turnOffRegion(faces, 3, 4, 2, 1);

        //front edges
        if (sampler.sample(neighbors, 1, 0, 1))
            turnOffRegion(faces, 0, 0, 5, 2);
        if (sampler.sample(neighbors, 1, 2, 1))
            turnOffRegion(faces, 0, 3, 5, 2);
        if (sampler.sample(neighbors, 0, 1, 1))
            turnOffRegion(faces, 0, 0, 2, 5);
        if (sampler.sample(neighbors, 2, 1, 1))
            turnOffRegion(faces, 3, 0, 2, 5);

        //front corners
        if (sampler.sample(neighbors, 0, 0, 1))
            turnOffRegion(faces, 0, 0, 2, 2);
        if (sampler.sample(neighbors, 2, 0, 1))
            turnOffRegion(faces, 3, 0, 2, 2);
        if (sampler.sample(neighbors, 0, 2, 1))
            turnOffRegion(faces, 0, 3, 2, 2);
        if (sampler.sample(neighbors, 2, 2, 1))
            turnOffRegion(faces, 3, 3, 2, 2);

        return faces;
    }

    private static boolean[][] newArr() {
        val ret = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                ret[i][j] = true;
            }
        }
        return ret;
    }

    private static void turnOffRegion(boolean[][] faces, int X, int Y, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                faces[Y + y][X + x] = false;
            }
        }
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
