/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.common.helper.BlockLightUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public abstract class BlockMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess world, int posX, int posY, int posZ) {
        var block = world.getBlock(posX, posY, posZ);
        var blockMeta = world.getBlockMetadata(posX, posY, posZ);

        var compactRGBLightValue = BlockLightUtil.getCompactRGBLightValue(world, block, blockMeta, posX, posY, posZ);
        var brightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ, compactRGBLightValue);

        if (brightness == 0 && block instanceof BlockSlab) {
            --posY;

            block = world.getBlock(posX, posY, posZ);
            blockMeta = world.getBlockMetadata(posX, posY, posZ);

            compactRGBLightValue = BlockLightUtil.getCompactRGBLightValue(world, block, blockMeta, posX, posY, posZ);
            brightness = world.getLightBrightnessForSkyBlocks(posX, posY, posZ, compactRGBLightValue);
        }

        return brightness;
    }
}
