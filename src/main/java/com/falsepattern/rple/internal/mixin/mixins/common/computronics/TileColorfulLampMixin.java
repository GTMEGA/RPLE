package com.falsepattern.rple.internal.mixin.mixins.common.computronics;

import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Pseudo
@Mixin(targets = "pl.asie.computronics.tile.TileColorfulLamp", remap = false)
public abstract class TileColorfulLampMixin implements RPLECustomBlockBrightness {
    @Shadow
    public abstract int getLampColor();

    @Override
    public short rple$getCustomBrightnessColor() {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(int i) {
        return LIGHT_VALUE_0.rgb16();
    }

    @Override
    public short rple$getCustomBrightnessColor(IBlockAccess access, int meta, int posX, int posY, int posZ) {
        short colour = (short) this.getLampColor();
        int red = ((colour >> 10) & 0x1f) >> 1;
        int green = ((colour >> 5) & 0x1f) >> 1;
        int blue = (colour & 0x1f) >> 1;
        System.out.println("RGB: " + red + ", " + green + ", " + blue);
        return ServerColorHelper.RGB16FromRGBChannel4Bit(red, green, blue);
    }
}
