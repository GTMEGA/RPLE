package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.storage.LumiBlockStorageRoot;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEBlockStorageRoot extends LumiBlockStorageRoot {
    @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel);

    @Nullable RPLEChunkRoot rple$getChunkRootFromBlockPosIfExists(int posX, int posZ);

    @Nullable RPLEChunkRoot rple$getChunkRootFromChunkPosIfExists(int chunkPosX, int chunkPosZ);
}
