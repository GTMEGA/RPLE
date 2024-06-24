/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.chunk;

import com.falsepattern.lumi.api.chunk.LumiChunk;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEChunk extends LumiChunk {
    @NotNull ColorChannel rple$channel();

    @Override
    @NotNull RPLEChunkRoot lumi$root();

    @Override
    @NotNull RPLEWorld lumi$world();

    @Override
    @Nullable RPLESubChunk lumi$getSubChunkIfPrepared(int chunkPosY);

    @Override
    @NotNull RPLESubChunk lumi$getSubChunk(int chunkPosY);
}
