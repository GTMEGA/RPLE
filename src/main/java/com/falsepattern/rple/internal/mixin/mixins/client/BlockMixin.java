package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.storage.ColoredBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(Block.class)
public abstract class BlockMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int l = world.getLightBrightnessForSkyBlocks(x, y, z, ((ColoredBlock)block).getLightValuePacked(world, x, y, z));

        if (l == 0 && block instanceof BlockSlab) {
            --y;
            block = world.getBlock(x, y, z);
            return world.getLightBrightnessForSkyBlocks(x, y, z, ((ColoredBlock)block).getLightValuePacked(world, x, y, z));
        } else {
            return l;
        }
    }
}
