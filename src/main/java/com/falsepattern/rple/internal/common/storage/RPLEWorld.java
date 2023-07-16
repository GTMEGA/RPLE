/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.lumina.api.world.LumiWorld;
import lombok.experimental.Accessors;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Accessors(fluent = true, chain = false)
public final class RPLEWorld implements LumiWorld {
    @Override
    public @NotNull RPLEWorldRoot lumi$root() {
        return null;
    }

    @Override
    public @NotNull String lumi$worldID() {
        return null;
    }

    @Override
    public @NotNull RPLEChunk lumi$wrap(@NotNull Chunk chunkBase) {
        return null;
    }

    @Override
    public @NotNull RPLESubChunk lumi$wrap(@NotNull ExtendedBlockStorage subChunkBase) {
        return null;
    }

    @Override
    public @Nullable RPLEChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ) {
        return null;
    }

    @Override
    public @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        return null;
    }

    @Override
    public @NotNull LumiLightingEngine lumi$lightingEngine() {
        return null;
    }

    @Override
    public int lumi$getBrightnessAndLightValueMax(@NotNull LightType lightType, int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getBrightnessAndBlockLightValueMax(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getLightValueMax(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType, int posX, int posY, int posZ, int lightValue) {

    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public void lumi$setBlockLightValue(int posX, int posY, int posZ, int lightValue) {

    }

    @Override
    public int lumi$getBlockLightValue(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public void lumi$setSkyLightValue(int posX, int posY, int posZ, int lightValue) {

    }

    @Override
    public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
        return 0;
    }
}
