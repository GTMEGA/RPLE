package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Utils;
import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static com.falsepattern.rple.internal.Utils.PACKED_BIT_BLOCK_COLOR;

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
        if ((minBlockLight & PACKED_BIT_BLOCK_COLOR) != 0) {
            minRed = (minBlockLight >> 8) & 0xF;
            minGreen = (minBlockLight >> 4) & 0xF;
            minBlue = minBlockLight & 0xF;
        } else {
            minRed = minGreen = minBlue = minBlockLight;
        }
        val carrier = ((ColoredCarrierWorld)this);
        val red = carrier.getColoredWorld(ColoredLightChannel.RED).getLightBrightnessForSkyBlocks(x, y, z, minRed);
        val green = carrier.getColoredWorld(ColoredLightChannel.GREEN).getLightBrightnessForSkyBlocks(x, y, z, minGreen);
        val blue = carrier.getColoredWorld(ColoredLightChannel.BLUE).getLightBrightnessForSkyBlocks(x, y, z, minBlue);
        return Utils.packedLongToCookie(Utils.lightsToPackedLong(red, green, blue));
    }
}
