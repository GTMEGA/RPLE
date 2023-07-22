/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.world.LumiWorldRoot;
import com.falsepattern.rple.api.common.color.ColorChannel;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface RPLEWorldRoot extends LumiWorldRoot {
    RPLEWorld rple$world(ColorChannel channel);

    @Deprecated
    int rple$getChannelBrightnessForTessellator(ColorChannel channel, int posX, int posY, int posZ, int minBlockLight);

    @Deprecated
    int rple$getChannelLightValueForTessellator(ColorChannel channel,
                                                LightType lightType,
                                                int posX,
                                                int posY,
                                                int posZ);

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
