package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.cache.LumiBlockCache;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockCache extends LumiBlockCache, RPLEBlockStorage {
    @NotNull RPLEBlockCacheRoot lumi$root();
}
