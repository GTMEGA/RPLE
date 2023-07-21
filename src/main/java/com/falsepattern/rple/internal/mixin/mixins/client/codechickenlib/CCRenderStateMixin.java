/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.codechickenlib;

import codechicken.lib.render.CCRenderState;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.mixin.helper.CodeChickenLibHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

// TODO: Suspected reason that CCL-rendered items have broken colors.
@Mixin(value = CCRenderState.class, remap = false)
public abstract class CCRenderStateMixin {
    @Shadow
    public static int brightness;

    @Shadow
    public static void setBrightness(int brightness) {}

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pullLightmap() {
        setBrightness(CookieMonster.packedLongToCookie(CodeChickenLibHelper.lastPackedBrightness()));
    }

    /**
     * @author FalsePattern
     * @reason Colorize
     */
    @Overwrite
    public static void pushLightmap() {
        CodeChickenLibHelper.setLightMapTextureCoordsPacked(CookieMonster.cookieToPackedLong(brightness));
    }
}
