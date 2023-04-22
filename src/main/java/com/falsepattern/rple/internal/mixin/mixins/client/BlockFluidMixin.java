package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;

@Mixin(BlockFluidBase.class)
public abstract class BlockFluidMixin {
    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        int lightThis     = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int lightUp       = world.getLightBrightnessForSkyBlocks(x, y + 1, z, 0);
        return Utils.packedMax(lightThis, lightUp);
    }
}
