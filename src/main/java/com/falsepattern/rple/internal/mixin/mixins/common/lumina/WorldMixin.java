package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import com.falsepattern.rple.internal.storage.ColoredLightWorld;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.World;

@Mixin(World.class)
public abstract class WorldMixin implements ColoredCarrierWorld {
    private ColoredLightWorld cRed;
    private ColoredLightWorld cGreen;
    private ColoredLightWorld cBlue;
    private boolean colorInit = false;
    @Override
    public ColoredLightWorld getColoredWorld(ColoredLightChannel channel) {
        if (!colorInit) {
            initColoredWorld();
            colorInit = true;
        }
        switch (channel) {
            case RED: return cRed;
            case GREEN: return cGreen;
            case BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }

    private void initColoredWorld() {
        cRed = new ColoredLightWorld((World) (Object) this, ColoredLightChannel.RED);
        cGreen = new ColoredLightWorld((World) (Object) this, ColoredLightChannel.GREEN);
        cBlue = new ColoredLightWorld((World) (Object) this, ColoredLightChannel.BLUE);
        colorInit = true;
    }
}
