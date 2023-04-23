package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//TODO test class
@Mixin(World.class)
public abstract class WorldMixin {
    /**
     * @author FalsePattern
     * @reason test
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minBlockLight) {
        return Utils.getLightBrightnessForSkyBlocksAccess((IBlockAccess) this, x, y, z, minBlockLight);
    }
}
