/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.chunk.LumiSubChunk;
import com.falsepattern.lumina.api.chunk.LumiSubChunkRoot;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.chunk.NibbleArray;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true, chain = false)
public final class RPLESubChunk implements LumiSubChunk {
    private final LumiSubChunk delegate;

    private final NibbleArray blockLight;
    @Nullable
    private final NibbleArray skyLight;

    public RPLESubChunk(LumiSubChunk delegate, NibbleArray blockLight, @Nullable NibbleArray skyLight) {
        this.delegate = delegate;

        this.blockLight = blockLight;
        this.skyLight = skyLight;
    }

    public RPLESubChunk(LumiSubChunk delegate, boolean hasSky) {
        this.delegate = delegate;

        this.blockLight = new NibbleArray(4096, 4);
        if (hasSky) {
            this.skyLight = new NibbleArray(4096, 4);
        } else {
            this.skyLight = null;
        }
    }

    @Override
    public LumiSubChunkRoot subChunkRoot() {
        return delegate.subChunkRoot();
    }
}
