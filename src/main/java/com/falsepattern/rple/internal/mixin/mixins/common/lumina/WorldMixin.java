/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.common.storage.ColoredLightWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(World.class)
public abstract class WorldMixin implements ColoredCarrierWorld {
    @Nullable
    private ColoredLightWorld cRed = null;
    @Nullable
    private ColoredLightWorld cGreen = null;
    @Nullable
    private ColoredLightWorld cBlue = null;

    private boolean colorInit = false;

    @Override
    public ColoredLightWorld coloredWorld(ColorChannel channel) {
        if (!colorInit) {
            initColoredWorld();
            colorInit = true;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                return cRed;
            case GREEN_CHANNEL:
                return cGreen;
            case BLUE_CHANNEL:
                return cBlue;
        }
    }

    private void initColoredWorld() {
        cRed = new ColoredLightWorld(thiz(), RED_CHANNEL);
        cGreen = new ColoredLightWorld(thiz(), GREEN_CHANNEL);
        cBlue = new ColoredLightWorld(thiz(), BLUE_CHANNEL);

        colorInit = true;
    }

    private World thiz() {
        return (World) (Object) this;
    }
}
