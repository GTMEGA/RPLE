package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.storage.LumiBlockStorage;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEBlockStorage extends LumiBlockStorage {
    @NotNull ColorChannel rple$channel();

    @Override
    @NotNull RPLEBlockStorageRoot lumi$root();

    @Override
    @NotNull RPLEWorld lumi$world();

    default int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
        return rple$getChannelBrightnessForTessellator(lumi$getChunkFromBlockPosIfExists(posX, posZ), posX, posY, posZ, minBlockLight);
    }

    int rple$getChannelBrightnessForTessellator(@Nullable RPLEChunk chunk, int posX, int posY, int posZ, int minBlockLight);

    default int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
        return rple$getChannelLightValueForRender(lumi$getChunkFromBlockPosIfExists(posX, posZ), lightType, posX, posY, posZ);
    }

    int rple$getChannelLightValueForRender(@Nullable RPLEChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ);
}
