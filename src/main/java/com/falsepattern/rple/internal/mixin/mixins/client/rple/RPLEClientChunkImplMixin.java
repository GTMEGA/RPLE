/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.rple;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.internal.client.storage.RPLEClientChunk;
import com.falsepattern.rple.internal.client.storage.RPLEClientSubChunk;
import lombok.val;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Unique
@Mixin(Chunk.class)
public abstract class RPLEClientChunkImplMixin implements RPLEClientChunk {
    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    @Override
    public int rple$getRGBLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkPosY = (posY & 255) >> 4;
        val subChunk = rple$getClientSubChunkIfPrepared(chunkPosY);
        if (subChunk == null)
            return 0; // TODO: Is this the correct fallback value?

        val subChunkPosY = posY & 15;
        return subChunk.rple$getRGBLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public int rple$getRGBLightValue(LightType type, int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkPosY = (posY & 255) >> 4;
        val subChunk = rple$getClientSubChunkIfPrepared(chunkPosY);
        if (subChunk == null)
            return 0; // TODO: Is this the correct fallback value?

        val subChunkPosY = posY & 15;
        return subChunk.rple$getRGBLightValue(type, subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @SuppressWarnings("InstanceofIncompatibleInterface")
    private @Nullable RPLEClientSubChunk rple$getClientSubChunkIfPrepared(int chunkPosY) {
        val subChunk = storageArrays[chunkPosY];
        if (subChunk instanceof RPLEClientSubChunk)
            return (RPLEClientSubChunk) subChunk;
        return null;
    }
}
