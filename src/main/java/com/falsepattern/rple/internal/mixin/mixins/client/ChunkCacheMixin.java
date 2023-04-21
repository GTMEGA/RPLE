package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//TODO test class
@Mixin(ChunkCache.class)
public abstract class ChunkCacheMixin {
    @Shadow private World worldObj;

    /**
     * @author FalsePattern
     * @reason test
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minSkyLight) {
        return worldObj.getLightBrightnessForSkyBlocks(x, y, z, minSkyLight);
    }
}
