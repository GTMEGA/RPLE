/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.client;

import com.falsepattern.rple.internal.Common;
import com.falsepattern.rple.internal.Utils;
import lombok.*;
import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.falsepattern.rple.internal.Utils.COOKIE_BIT;

@Mixin(OpenGlHelper.class)
public abstract class OpenGLHelperMixin {
    @Shadow public static int lightmapTexUnit;

    @Shadow
    public static void setLightmapTextureCoords(int p_77475_0_, float p_77475_1_, float p_77475_2_) {
    }

    @Inject(method = "setLightmapTextureCoords",
            at = @At(value = "HEAD"),
            cancellable = true,
            require = 1)
    private static void onSet(int texture, float u, float v, CallbackInfo ci) {
        int value = (int)(u) | ((int)(v) << 16);
        if ((value & COOKIE_BIT) != 0) {
            long packed = Utils.cookieToPackedLong(value);
            val red = Utils.getRedPair(packed);
            val green = Utils.getGreenPair(packed);
            val blue = Utils.getBluePair(packed);
            setLightmapTextureCoords(Common.RED_LIGHT_MAP_TEXTURE_UNIT, red & 0xFFFF, red >>> 16);
            setLightmapTextureCoords(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, green & 0xFFFF, green >>> 16);
            setLightmapTextureCoords(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, blue & 0xFFFF, blue >>> 16);
            ci.cancel();
        } else {
            if (texture == lightmapTexUnit) {
                if (lightmapTexUnit != Common.RED_LIGHT_MAP_TEXTURE_UNIT) {
                    setLightmapTextureCoords(Common.RED_LIGHT_MAP_TEXTURE_UNIT, u, v);
                }
                setLightmapTextureCoords(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, u, v);
                setLightmapTextureCoords(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, u, v);
            }
        }
    }
}
