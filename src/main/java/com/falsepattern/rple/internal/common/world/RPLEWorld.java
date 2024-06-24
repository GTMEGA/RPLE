/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumi.api.world.LumiWorld;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorage;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunk;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEWorld extends LumiWorld, RPLEBlockStorage {
    @Override
    @NotNull RPLEWorldRoot lumi$root();

    RPLEWorld getCloneForChunkCache(RPLEBlockStorageRoot chunkCache);

    @Override
    @NotNull RPLEChunk lumi$wrap(@NotNull Chunk chunkBase);

    @Override
    @NotNull RPLESubChunk lumi$wrap(@NotNull ExtendedBlockStorage subChunkBase);
}
