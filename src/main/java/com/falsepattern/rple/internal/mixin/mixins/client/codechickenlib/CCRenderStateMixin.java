/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.codechickenlib;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.internal.client.render.CookieMonster;
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// TODO: Suspected reason that CCL-rendered items have broken colors.
@Mixin(value = CCRenderState.class, remap = false)
public abstract class CCRenderStateMixin {

    @Shadow
    public static void setBrightness(int brightness) {}

    @Shadow
    public static int brightness() {
        return 0;
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pullLightmap() {
        setBrightness(CookieMonster.packedLongToCookie(ExtendedOpenGlHelper.lastPackedBrightness()));
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pushLightmap() {
        ExtendedOpenGlHelper.setLightMapTextureCoordsPacked(CookieMonster.cookieToPackedLong(brightness()));
    }
}
