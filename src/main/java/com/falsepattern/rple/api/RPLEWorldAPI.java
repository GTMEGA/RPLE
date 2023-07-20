/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.api.RPLEColorAPI.*;
import static com.falsepattern.rple.api.color.ColorChannel.*;

public final class RPLEWorldAPI {
    private RPLEWorldAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getChannelLightValue(IBlockAccess world,
                                           ColorChannel channel,
                                           int posX,
                                           int posY,
                                           int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return channel.componentFromColor(errorColor());
        return worldRoot.rple$world(channel).lumi$getLightValue(posX, posY, posZ);
    }

    public static int getChannelLightValue(IBlockAccess world,
                                           ColorChannel channel,
                                           LightType lightType,
                                           int posX,
                                           int posY,
                                           int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return channel.componentFromColor(errorColor());
        return worldRoot.rple$world(channel).lumi$getLightValue(lightType, posX, posY, posZ);
    }

    public static int getChannelBlockLightValue(IBlockAccess world,
                                                ColorChannel channel,
                                                int posX,
                                                int posY,
                                                int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return channel.componentFromColor(errorColor());
        return worldRoot.rple$world(channel).lumi$getBlockLightValue(posX, posY, posZ);
    }

    public static int getChannelSkyLightValue(IBlockAccess world, ColorChannel channel, int posX, int posY, int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return channel.componentFromColor(errorColor());
        return worldRoot.rple$world(channel).lumi$getSkyLightValue(posX, posY, posZ);
    }

    public static int getGreyscaleLightValue(IBlockAccess world,
                                             int posX,
                                             int posY,
                                             int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return COLOR_MIN;

        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getGreyscaleLightValue(IBlockAccess world,
                                             LightType lightType,
                                             int posX,
                                             int posY,
                                             int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return COLOR_MIN;

        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getLightValue(lightType, posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getLightValue(lightType, posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getLightValue(lightType, posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getGreyscaleBlockLightValue(IBlockAccess world,
                                                  int posX,
                                                  int posY,
                                                  int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return COLOR_MIN;

        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getBlockLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    public static int getGreyscaleSkyLightValue(IBlockAccess world,
                                                int posX,
                                                int posY,
                                                int posZ) {
        val worldRoot = getWorldRootFromBlockAccess(world);
        if (worldRoot == null)
            return COLOR_MIN;

        val redLightValue = worldRoot.rple$world(RED_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        val greenLightValue = worldRoot.rple$world(GREEN_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        val blueLightValue = worldRoot.rple$world(BLUE_CHANNEL).lumi$getSkyLightValue(posX, posY, posZ);
        return maxColorComponent(redLightValue, greenLightValue, blueLightValue);
    }

    // World Brightness
    // Block
    // Sky
    // Mixed

    // World Translucency

    @SuppressWarnings("InstanceofIncompatibleInterface")
    static @Nullable RPLEWorldRoot getWorldRootFromBlockAccess(IBlockAccess blockAccess) {
        if (blockAccess == null)
            return null;
        if (blockAccess instanceof RPLEWorldRoot)
            return (RPLEWorldRoot) blockAccess;
        if (blockAccess instanceof ChunkCache) {
            final ChunkCache chunkCache = (ChunkCache) blockAccess;
            final World worldBase = chunkCache.worldObj;
            if (worldBase instanceof RPLEWorldRoot)
                return (RPLEWorldRoot) worldBase;
        }
        return null;
    }
}
