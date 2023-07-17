/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.appliedenergistics2;

import appeng.api.util.AEColor;
import appeng.helpers.AEMultiTile;
import appeng.parts.CableBusContainer;
import appeng.parts.CableBusStorage;
import appeng.parts.ICableBusContainer;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.mixin.interfaces.ae2.ICableBusContainerMixin;
import lombok.val;
import lombok.var;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CableBusContainer.class,
       remap = false)
public abstract class CableBusContainerMixin extends CableBusStorage implements AEMultiTile,
                                                                                ICableBusContainer,
                                                                                ICableBusContainerMixin {
    @Shadow
    public abstract AEColor getColor();

    @Override
    public int getColoredLightValue(ColorChannel channel) {
        var light = 0;
        for (val direction : ForgeDirection.values()) {
            val part = getPart(direction);
            if (part != null)
                light = Math.max(part.getLightLevel(), light);
        }

        if (light < 1)
            return 0;

        var color = getColor().mediumVariant;
        switch (channel) {
            default:
            case RED_CHANNEL:
                color = (color >>> 16) & 0xff;
                break;
            case GREEN_CHANNEL:
                color = (color >>> 8) & 0xff;
                break;
            case BLUE_CHANNEL:
                color = color & 0xff;
                break;
        }

        return (int) (((float) color / 255F) * (float) light);
    }
}
