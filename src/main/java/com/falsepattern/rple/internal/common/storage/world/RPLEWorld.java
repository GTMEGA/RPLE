/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage.world;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.storage.chunk.RPLESubChunk;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEWorld extends LumiWorld {
    @Override
    @NotNull RPLEWorldRoot lumi$root();

    @Override
    @NotNull RPLEChunk lumi$wrap(@NotNull Chunk chunkBase);

    @Override
    @NotNull RPLESubChunk lumi$wrap(@NotNull ExtendedBlockStorage subChunkBase);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ);

    int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight);

    int rple$getChannelLightValueForRender(LightType lightType, int posX, int posY, int posZ);
}
