package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//TODO test class
@Mixin(ChunkCache.class)
public abstract class ChunkCacheMixin {

    /**
     * @author FalsePattern
     * @reason test
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minSkyLight) {
        return Utils.getLightBrightnessForSkyBlocksAccess((IBlockAccess)this, x, y, z, minSkyLight);
    }
}
