package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.EnumSkyBlock;
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
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minSkyLight) {
        return ((ColoredCarrierWorld)this).getColoredWorld(ColoredLightChannel.RED).getLightBrightnessForSkyBlocks(x, y, z, minSkyLight);
    }
}
