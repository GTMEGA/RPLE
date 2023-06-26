/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.helper;

import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.mixin.interfaces.IBlockColorizerMixin;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static com.falsepattern.rple.api.color.ColorChannel.*;

/**
 * Shared logic used by multiple mixins, couldn't find a better place for these.
 */
public class BlockLightUtil {
    /**
     * The method that makes all of this work. Replaces the regular brightness logic with our custom packed/cookie system.
     */
    public static int getRGBBrightnessAt(IBlockAccess world, int posX, int posY, int posZ, int minBlockLight) {
        val packedMinBrightness = CookieMonster.cookieToPackedLong(minBlockLight);
        val minRedBrightness = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessRed(packedMinBrightness));
        val minGreenBrightness = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessGreen(packedMinBrightness));
        val minBlueBrightness = BrightnessUtil.getBlocklightFromBrightness(BrightnessUtil.getBrightnessBlue(packedMinBrightness));

        final ColoredCarrierWorld carrier;
        if (world instanceof World) {
            carrier = (ColoredCarrierWorld) world;
        } else {
            carrier = (ColoredCarrierWorld) (((ChunkCache) world).worldObj);
        }

        val red = carrier.coloredWorld(RED_CHANNEL)
                         .getLightBrightnessForSkyBlocksWorld(world, posX, posY, posZ, minRedBrightness);
        val green = carrier.coloredWorld(ColorChannel.GREEN_CHANNEL)
                           .getLightBrightnessForSkyBlocksWorld(world, posX, posY, posZ, minGreenBrightness);
        val blue = carrier.coloredWorld(ColorChannel.BLUE_CHANNEL)
                          .getLightBrightnessForSkyBlocksWorld(world, posX, posY, posZ, minBlueBrightness);

        return CookieMonster.packedLongToCookie(BrightnessUtil.brightnessesToPackedLong(red, green, blue));
    }

    public static int getCompactRGBLightValue(IBlockAccess world,
                                              Block block,
                                              int blockMeta,
                                              int posX,
                                              int posY,
                                              int posZ) {
        // TODO: [PRE-RELEASE] fix thisss
        val colorizedBlock = (IBlockColorizerMixin) block;
        val color = colorizedBlock.rple$getColoredBrightness(world, blockMeta, posX, posY, posZ);

        val red = RED_CHANNEL.componentFromColor(color);
        val green = GREEN_CHANNEL.componentFromColor(color);
        val blue = BLUE_CHANNEL.componentFromColor(color);

        // TODO: [PRE-RELEASE] 100% broken now.
        // This is here if we already generated a packed value somewhere deep inside another mod
        // Current usages:
        // AE2 CLApi
        if (CookieMonster.inspectValue(red) == CookieMonster.IntType.COOKIE)
            return red;

        return createCompactRGBLightValue(red, green, blue);
    }

    public static int createCompactRGBLightValue(int r, int g, int b) {
        val R = BrightnessUtil.lightLevelsToBrightness(r, 0);
        val G = BrightnessUtil.lightLevelsToBrightness(g, 0);
        val B = BrightnessUtil.lightLevelsToBrightness(b, 0);
        val packed = BrightnessUtil.brightnessesToPackedLong(R, G, B);
        return CookieMonster.packedLongToCookie(packed);
    }
}
