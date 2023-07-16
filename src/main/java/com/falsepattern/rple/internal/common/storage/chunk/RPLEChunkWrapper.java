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

package com.falsepattern.rple.internal.common.storage.chunk;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.internal.common.storage.world.RPLEWorld;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public final class RPLEChunkWrapper implements RPLEChunk {
    @Override
    public @NotNull RPLEChunkRoot lumi$root() {
        return null;
    }

    @Override
    public @NotNull RPLEWorld lumi$world() {
        return null;
    }

    @Override
    public @NotNull String lumi$chunkID() {
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
    public @Nullable RPLESubChunk lumi$getSubChunkIfPrepared(int chunkPosY) {
        return null;
    }

    @Override
    public @NotNull RPLESubChunk lumi$getSubChunk(int chunkPosY) {
        return null;
    }

    @Override
    public int lumi$chunkPosX() {
        return 0;
    }

    @Override
    public int lumi$chunkPosZ() {
        return 0;
    }

    @Override
    public void lumi$queuedRandomLightUpdates(int remainingRandomLightUpdates) {

    }

    @Override
    public int lumi$queuedRandomLightUpdates() {
        return 0;
    }

    @Override
    public int lumi$getBrightnessAndLightValueMax(@NotNull LightType lightType,
                                                  int subChunkPosX,
                                                  int posY,
                                                  int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getBrightnessAndBlockLightValueMax(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getLightValueMax(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType,
                                   int subChunkPosX,
                                   int posY,
                                   int subChunkPosZ,
                                   int lightValue) {

    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$setBlockLightValue(int subChunkPosX, int posY, int subChunkPosZ, int lightValue) {

    }

    @Override
    public int lumi$getBlockLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$setSkyLightValue(int subChunkPosX, int posY, int subChunkPosZ, int lightValue) {

    }

    @Override
    public int lumi$getSkyLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockBrightness(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockOpacity(int subChunkPosX, int posY, int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockBrightness(@NotNull Block block,
                                       int blockMeta,
                                       int subChunkPosX,
                                       int posY,
                                       int subChunkPosZ) {
        return 0;
    }

    @Override
    public int lumi$getBlockOpacity(@NotNull Block block,
                                    int blockMeta,
                                    int subChunkPosX,
                                    int posY,
                                    int subChunkPosZ) {
        return 0;
    }

    @Override
    public boolean lumi$canBlockSeeSky(int subChunkPosX, int posY, int subChunkPosZ) {
        return false;
    }

    @Override
    public void lumi$skyLightHeight(int subChunkPosX, int subChunkPosZ, int skyLightHeight) {

    }

    @Override
    public int lumi$skyLightHeight(int subChunkPosX, int subChunkPosZ) {
        return 0;
    }

    @Override
    public void lumi$minSkyLightHeight(int minSkyLightPosY) {

    }

    @Override
    public int lumi$minSkyLightHeight() {
        return 0;
    }

    @Override
    public void lumi$resetSkyLightHeightMap() {

    }

    @Override
    public void lumi$isHeightOutdated(int subChunkPosX, int subChunkPosZ, boolean isHeightOutdated) {

    }

    @Override
    public boolean lumi$isHeightOutdated(int subChunkPosX, int subChunkPosZ) {
        return false;
    }

    @Override
    public void lumi$resetOutdatedHeightFlags() {

    }

    @Override
    public void lumi$isLightingInitialized(boolean lightingInitialized) {

    }

    @Override
    public boolean lumi$isLightingInitialized() {
        return false;
    }

    @Override
    public void lumi$resetLighting() {

    }
}
