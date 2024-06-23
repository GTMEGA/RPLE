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
import com.falsepattern.rple.api.common.ServerColorHelper;
import com.falsepattern.rple.internal.mixin.interfaces.appliedenergistics2.ICableBusContainerMixin;
import lombok.val;
import lombok.var;
import net.minecraftforge.common.util.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.common.color.LightValueColor.LIGHT_VALUE_0;

@Mixin(value = CableBusContainer.class,
       remap = false)
public abstract class CableBusContainerMixin extends CableBusStorage implements AEMultiTile,
                                                                                ICableBusContainer,
                                                                                ICableBusContainerMixin {
    @Shadow
    public abstract AEColor getColor();

    @Override
    public short rple$getColoredBrightness() {
        var light = 0;
        for (val direction : ForgeDirection.values()) {
            val part = getPart(direction);
            if (part != null)
                light = Math.max(part.getLightLevel(), light);
        }
        if (light < 1)
            return LIGHT_VALUE_0.rgb16();

        val colorRGB = getColor().mediumVariant;
        val red = (int) ((float) ((colorRGB >>> 16) & 0xFF) / 255F * (float) light) & 0xF;
        val green = (int) ((float) ((colorRGB >>> 8) & 0xFF) / 255F * (float) light) & 0xF;
        val blue = (int) ((float) (colorRGB & 0xFF) / 255F * (float) light) & 0xF;
        return ServerColorHelper.RGB16FromRGBChannel4Bit(red, green, blue);
    }
}
