package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class BlockCaches {
    public static RPLEBlockCacheRoot createFallbackBlockCacheRoot(@NotNull RPLEWorldRoot worldRoot) {
        return worldRoot.lumi$isClientSide() ?
               new ReadThroughBlockCacheRoot(worldRoot) : new FocusedBlockCacheRoot(worldRoot);
    }
}
