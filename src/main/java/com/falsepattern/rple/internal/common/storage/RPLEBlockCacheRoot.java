package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.storage.LumiBlockCacheRoot;
import com.falsepattern.rple.api.common.color.ColorChannel;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockCacheRoot extends LumiBlockCacheRoot, RPLEBlockStorageRoot {
    @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel);
}
