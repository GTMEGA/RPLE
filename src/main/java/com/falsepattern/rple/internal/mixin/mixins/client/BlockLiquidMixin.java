/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.common.helper.color.CookieWrappers;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLiquid;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockLiquid.class)
public abstract class BlockLiquidMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        int lightThis     = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int lightUp       = world.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
        return CookieWrappers.packedMax(lightThis, lightUp);
    }
}
