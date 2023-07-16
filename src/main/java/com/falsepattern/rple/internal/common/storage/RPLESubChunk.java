/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.chunk.LumiSubChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

@Getter
@Accessors(fluent = true, chain = false)
public final class RPLESubChunk implements LumiSubChunk {

    @Override
    public @NotNull RPLESubChunkRoot lumi$root() {
        return null;
    }

    @Override
    public @NotNull String lumi$subChunkID() {
        return null;
    }

    @Override
    public void lumi$writeToNBT(@NotNull NBTTagCompound output) {

    }

    @Override
    public void lumi$readFromNBT(@NotNull NBTTagCompound input) {

    }

    @Override
    public void lumi$writeToPacket(@NotNull ByteBuffer output) {

    }

    @Override
    public void lumi$readFromPacket(@NotNull ByteBuffer input) {

    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType,
                                   int subChunkPosX,
                                   int subChunkPosY,
                                   int subChunkPosZ,
                                   int lightValue) {

    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$setBlockLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ, int lightValue) {

    }

    @Override
    public int lumi$getBlockLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$setSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ, int lightValue) {

    }

    @Override
    public int lumi$getSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return 0;
    }
}
