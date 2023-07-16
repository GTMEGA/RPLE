/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.common.storage.world;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunkWrapper;
import com.falsepattern.rple.internal.common.storage.chunk.RPLESubChunkWrapper;
import lombok.experimental.Accessors;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Accessors(fluent = true, chain = false)
public final class RPLEWorldWrapper implements RPLEWorld {
    @Override
    public @NotNull RPLEWorldRoot lumi$root() {
        return null;
    }

    @Override
    public @NotNull String lumi$worldID() {
        return null;
    }

    @Override
    public @NotNull RPLEChunkWrapper lumi$wrap(@NotNull Chunk chunkBase) {
        return null;
    }

    @Override
    public @NotNull RPLESubChunkWrapper lumi$wrap(@NotNull ExtendedBlockStorage subChunkBase) {
        return null;
    }

    @Override
    public @Nullable RPLEChunkWrapper lumi$getChunkFromBlockPosIfExists(int posX, int posZ) {
        return null;
    }

    @Override
    public @Nullable RPLEChunkWrapper lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
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
