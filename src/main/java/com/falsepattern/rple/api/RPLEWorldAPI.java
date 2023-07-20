/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorldRoot;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class RPLEWorldAPI {
    private RPLEWorldAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getMaxBrightness() {
        return 0;
    }

    public static int getBrightness(ColorChannel channel) {
        return 0;
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
