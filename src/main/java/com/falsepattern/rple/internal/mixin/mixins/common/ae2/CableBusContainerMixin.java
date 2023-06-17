/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.common.ae2;

import appeng.api.parts.IPart;
import appeng.api.util.AEColor;
import appeng.helpers.AEMultiTile;
import appeng.parts.CableBusContainer;
import appeng.parts.CableBusStorage;
import appeng.parts.ICableBusContainer;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.mixin.interfaces.ae2.ICableBusContainerMixin;
import lombok.var;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraftforge.common.util.ForgeDirection;

@Mixin(value = CableBusContainer.class,
       remap = false)
public abstract class CableBusContainerMixin extends CableBusStorage implements AEMultiTile, ICableBusContainer,
        ICableBusContainerMixin {
    @Shadow public abstract AEColor getColor();

    @Override
    public int getColoredLightValue(int colorChannel) {
        int light = 0;
        ForgeDirection[] arr$ = ForgeDirection.values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection d = arr$[i$];
            IPart p = this.getPart(d);
            if (p != null) {
                light = Math.max(p.getLightLevel(), light);
            }
        }

        if (light > 0) {
            var color = this.getColor().mediumVariant;
            switch (colorChannel) {
                case LightConstants.COLOR_CHANNEL_RED:
                    color = (color >>> 16) & 0xff;
                    break;
                case LightConstants.COLOR_CHANNEL_GREEN:
                    color = (color >>> 8) & 0xff;
                    break;
                case LightConstants.COLOR_CHANNEL_BLUE:
                    color = color & 0xff;
                    break;
                default:
                    color = 0;
            }
            return (int)((color / 255f) * light);
        } else {
            return 0;
        }
    }
}
