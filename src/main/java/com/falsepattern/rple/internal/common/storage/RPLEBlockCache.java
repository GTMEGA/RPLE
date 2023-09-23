package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.storage.LumiBlockCache;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockCache extends LumiBlockCache, RPLEBlockStorage {
    @NotNull RPLEBlockCacheRoot lumi$root();
}
