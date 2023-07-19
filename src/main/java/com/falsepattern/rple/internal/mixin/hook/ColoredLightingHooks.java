/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.mixin.hook;

import com.falsepattern.rple.api.RPLERenderAPI;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import com.falsepattern.rple.internal.common.helper.CookieMonster;
import com.falsepattern.rple.internal.common.helper.CookieWrappers;
import com.falsepattern.rple.internal.common.helper.EntityHelper;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

@UtilityClass
public final class ColoredLightingHooks {
    public static int getEntityRGBBrightnessForTessellator(Entity entity,
                                                           IBlockAccess world,
                                                           int posX,
                                                           int posY,
                                                           int posZ,
                                                           int cookieMinBlockLight) {
        var brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, cookieMinBlockLight);
        if (EntityHelper.isOnBlockList(entity.getClass())) {
            val packedBrightness = CookieMonster.cookieToPackedLong(brightness);
            brightness = BrightnessUtil.getBrightestChannelFromPacked(packedBrightness);
        }
        return brightness;
    }

    public static int getBlockRGBBrightnessForTessellator(IBlockAccess world,
                                                          int posX,
                                                          int posY,
                                                          int posZ) {
        var block = world.getBlock(posX, posY, posZ);
        var blockMeta = world.getBlockMetadata(posX, posY, posZ);

        var brightness = RPLERenderAPI.getBlockBrightnessForTessellator(world, block, blockMeta, posX, posY, posZ);
        brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightness);

        if (brightness == 0 && block instanceof BlockSlab) {
            --posY;

            block = world.getBlock(posX, posY, posZ);
            blockMeta = world.getBlockMetadata(posX, posY, posZ);

            brightness = RPLERenderAPI.getBlockBrightnessForTessellator(world, block, blockMeta, posX, posY, posZ);
            brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, brightness);
        }

        return brightness;
    }

    public static int getBlockFluidRGBBrightnessForTessellator(IBlockAccess world,
                                                               int posX,
                                                               int posY,
                                                               int posZ) {
        val brightness = getRGBBrightnessForTessellator(world, posX, posY, posZ, 0);
        val topBrightness = getRGBBrightnessForTessellator(world, posX, posY + 1, posZ, 0);
        return CookieWrappers.packedMax(brightness, topBrightness);
    }

    public static int getRGBBrightnessForTessellator(IBlockAccess world,
                                                     int posX,
                                                     int posY,
                                                     int posZ,
                                                     int cookieMinBlockLight) {
        val packedMinBrightness = CookieMonster.cookieToPackedLong(cookieMinBlockLight);

        val minRedBrightness = BrightnessUtil.getBrightnessRed(packedMinBrightness);
        val minGreenBrightness = BrightnessUtil.getBrightnessGreen(packedMinBrightness);
        val minBlueBrightness = BrightnessUtil.getBrightnessBlue(packedMinBrightness);

        val minRedBlockLight = BrightnessUtil.getBlockLightFromBrightness(minRedBrightness);
        val minGreenBlockLight = BrightnessUtil.getBlockLightFromBrightness(minGreenBrightness);
        val minBlueBlockLight = BrightnessUtil.getBlockLightFromBrightness(minBlueBrightness);

        return RPLERenderAPI.getRGBBrightnessForTessellator(world,
                                                            posX,
                                                            posY,
                                                            posZ,
                                                            minRedBlockLight,
                                                            minGreenBlockLight,
                                                            minBlueBlockLight);
    }
}
