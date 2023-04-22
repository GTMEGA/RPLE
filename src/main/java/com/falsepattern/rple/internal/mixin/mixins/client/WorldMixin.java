package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import lombok.val;
import lombok.var;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static com.falsepattern.rple.internal.Utils.MASK;

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
        int minRed, minGreen, minBlue;
        if ((minBlockLight & MASK) != 0) {
            minRed = (minBlockLight >> 8) & 0xF;
            minGreen = (minBlockLight >> 4) & 0xF;
            minBlue = minBlockLight & 0xF;
        } else {
            minRed = minGreen = minBlue = minBlockLight;
        }
        val carrier = ((ColoredCarrierWorld)this);
        val red = carrier.getColoredWorld(ColoredLightChannel.RED).getLightBrightnessForSkyBlocks(x, y, z, minRed) & 0x3FF;
        val green = carrier.getColoredWorld(ColoredLightChannel.GREEN).getLightBrightnessForSkyBlocks(x, y, z, minGreen) & 0x3FF;
        val blue = carrier.getColoredWorld(ColoredLightChannel.BLUE).getLightBrightnessForSkyBlocks(x, y, z, minBlue) & 0x3FF;
        val color = red << 20 | green << 10 | blue;
        return MASK | (color & 0x3FFFFFFF);
    }

    //
    // 3322 2222 2222 1111 1111 1100 0000 0000
    // 1098 7654 3210 9876 5432 1098 7654 3210
    // Vanilla:
    // 0000 0000 AAAA 0000 0000 0000 aaaa 0000
    // Colored:
    // 10RR RRRr rrrr GGGG Gggg ggBB BBBb bbbb
}
