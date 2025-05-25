/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.IBlockAccess;

import static com.falsepattern.rple.api.common.ServerColorHelper.maxColorComponent;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

// TODO: [LATER_BUT_NOT_TOO_LATER] Decide what to do?
/**
 * Currently only used by {@link LightValueOverlayRenderer}
 */
public final class RPLEWorldUtil {
    private RPLEWorldUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getChannelBlockLightValue(IBlockAccess world,
                                                ColorChannel channel,
                                                int posX,
                                                int posY,
                                                int posZ) {
        val worldRoot = (RPLEWorldRoot) world;
        return worldRoot.rple$world(channel).lumi$getBlockLightValue(posX, posY, posZ);
    }

    public static int getChannelSkyLightValue(IBlockAccess world, ColorChannel channel, int posX, int posY, int posZ) {
        val worldRoot = (RPLEWorldRoot) world;
        return worldRoot.rple$world(channel).lumi$getSkyLightValue(posX, posY, posZ);
    }

    public static int getGreyscaleLightValue(IBlockAccess world,
                                             int posX,
                                             int posY,
                                             int posZ) {
        val worldRoot = (RPLEWorldRoot) world;
        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }


    public static int getGreyscaleBlockLightValue(IBlockAccess world,
                                                  int posX,
                                                  int posY,
                                                  int posZ) {
        val worldRoot = (RPLEWorldRoot) world;
        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getGreyscaleSkyLightValue(IBlockAccess world,
                                                int posX,
                                                int posY,
                                                int posZ) {
        val worldRoot = (RPLEWorldRoot) world;
        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }
//
//    public static int getChannelLightOpacity(IBlockAccess world, ColorChannel channel, int posX, int posY, int posZ) {
//        val worldRoot = rple$getWorldRootFromBlockAccess(world);
//        if (worldRoot == null)
//            return channel.componentFromColor(errorColor());
//        return worldRoot.rple$world(channel).lumi$getBlockOpacity(posX, posY, posZ);
//    }
//
//    public static int getGreyscaleLightOpacity(IBlockAccess world,
//                                               int posX,
//                                               int posY,
//                                               int posZ) {
//        val worldRoot = rple$getWorldRootFromBlockAccess(world);
//        if (worldRoot == null)
//            return COLOR_MIN;
//
//        val redLightOpacity = worldRoot.rple$world(RED_CHANNEL).lumi$getBlockOpacity(posX, posY, posZ);
//        val greenLightOpacity = worldRoot.rple$world(GREEN_CHANNEL).lumi$getBlockOpacity(posX, posY, posZ);
//        val blueLightOpacity = worldRoot.rple$world(BLUE_CHANNEL).lumi$getBlockOpacity(posX, posY, posZ);
//        return minColorComponent(redLightOpacity, greenLightOpacity, blueLightOpacity);
//    }
}
