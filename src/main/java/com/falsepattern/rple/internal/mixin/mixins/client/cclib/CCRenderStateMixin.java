/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.cclib;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.internal.color.CookieMonster;
import com.falsepattern.rple.internal.mixin.helpers.OpenGlHelperPacked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CCRenderState.class,
       remap = false)
public abstract class CCRenderStateMixin {

    @Shadow public static void setBrightness(int b) {

    }

    @Shadow public static int brightness;

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pullLightmap() {
        setBrightness(CookieMonster.packedLongToCookie(OpenGlHelperPacked.prevLightValuePacked));
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pushLightmap() {
        OpenGlHelperPacked.setLightMapTextureCoordsPacked(CookieMonster.cookieToPackedLong(brightness));
    }
}
