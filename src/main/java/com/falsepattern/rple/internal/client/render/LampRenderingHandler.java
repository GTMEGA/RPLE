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
import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.blocks.Lamp;
import lombok.val;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        drawCube(tessellator, renderer, block, 0, 0, 0, block.getIcon(0, 0));
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        val lamp = (Lamp)block;
        val tessellator = Compat.tessellator();
        switch (((ToggleableTessellator)tessellator).pass()) {
            case 0:
                return renderer.renderStandardBlock(lamp, x, y, z);
            case 1:
                val meta = world.getBlockMetadata(x, y, z);
                if (lamp.powered) {
                    val r = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z) * 17;
                    val g = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z) * 17;
                    val b = lamp.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z) * 17;
                    tessellator.setBrightness(0x00F000F0);
                    tessellator.setColorRGBA(r, g, b, 128);
                    renderer.setRenderBounds(1F + OFFSET, -OFFSET, 1F + OFFSET, -OFFSET, 1F + OFFSET, -OFFSET);
                    drawCube(tessellator, renderer, block, x, y, z, lamp.getGlowIcon());
                }
                return false;
            default:
                return false;
        }
    }

    private static void drawCube(Tessellator tessellator, RenderBlocks renderer, Block block, double x, double y, double z, IIcon texture) {
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

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return RENDER_ID;
    }
}
