package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.storage.LumiBlockStorageRoot;
import com.falsepattern.rple.api.common.color.ColorChannel;
import org.jetbrains.annotations.NotNull;

public interface RPLEBlockStorageRoot extends LumiBlockStorageRoot {
    @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel);
}
