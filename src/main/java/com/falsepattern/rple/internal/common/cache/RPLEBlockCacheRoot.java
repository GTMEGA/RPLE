package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.cache.LumiBlockCacheRoot;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.common.color.ColorChannel;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockCacheRoot extends LumiBlockCacheRoot, RPLEBlockStorageRoot {
    @NotNull RPLEBlockCache lumi$createBlockCache(LumiWorld world);

    @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel);
}
