/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.common.storage.ColoredLightWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements ColoredCarrierWorld {
    private ColoredLightWorld cRed;
    private ColoredLightWorld cGreen;
    private ColoredLightWorld cBlue;
    private boolean colorInit = false;
    @Override
    public ColoredLightWorld getColoredWorld(int colorChannel) {
        if (!colorInit) {
            initColoredWorld();
            colorInit = true;
        }
        switch (colorChannel) {
            case LightConstants.COLOR_CHANNEL_RED: return cRed;
            case LightConstants.COLOR_CHANNEL_GREEN: return cGreen;
            case LightConstants.COLOR_CHANNEL_BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }

    private void initColoredWorld() {
        cRed = new ColoredLightWorld((World) (Object) this, LightConstants.COLOR_CHANNEL_RED);
        cGreen = new ColoredLightWorld((World) (Object) this, LightConstants.COLOR_CHANNEL_GREEN);
        cBlue = new ColoredLightWorld((World) (Object) this, LightConstants.COLOR_CHANNEL_BLUE);
        colorInit = true;
    }
}
