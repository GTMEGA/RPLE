/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.chunk;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.lumina.api.lighting.LightType.SKY_LIGHT_TYPE;

public final class RPLEChunkContainer implements RPLEChunk {
    private final ColorChannel channel;
    private final String chunkID;
    private final RPLEWorld world;
    private final RPLEChunkRoot root;
    private final LumiChunk lumiChunk;

    private final int chunkPosX;
    private final int chunkPosZ;
    private final int[] skyLightHeightMap;
    private final boolean[] outdatedSkylightColumns;

    private int minSkyLightHeight;
    private int queuedRandomLightUpdates;
    private boolean isSkyLightHeightMapValid;
    private boolean isFullyLit;

    public RPLEChunkContainer(ColorChannel channel, RPLEWorldRoot worldRoot, RPLEChunkRoot root, LumiChunk lumiChunk) {
        this.channel = channel;
        this.chunkID = Tags.MOD_ID + "_" + channel + "_chunk";
        this.world = worldRoot.rple$world(channel);
        this.lumiChunk = lumiChunk;
        this.root = root;

        this.chunkPosX = lumiChunk.lumi$chunkPosX();
        this.chunkPosZ = lumiChunk.lumi$chunkPosZ();
        this.skyLightHeightMap = new int[HEIGHT_MAP_ARRAY_SIZE];
        this.outdatedSkylightColumns = new boolean[HEIGHT_MAP_ARRAY_SIZE];

        this.minSkyLightHeight = Integer.MAX_VALUE;
        this.queuedRandomLightUpdates = 0;
        this.isFullyLit = false;
    }

    public RPLEChunkContainer(ColorChannel channel,
                              RPLEWorldRoot worldRoot,
                              RPLEChunkRoot root,
                              LumiChunk lumiChunk,
                              int[] skyLightHeightMap,
                              boolean[] outdatedSkylightColumns) {
        this.channel = channel;
        this.chunkID = Tags.MOD_ID + "_" + channel + "_chunk";
        this.world = worldRoot.rple$world(channel);
        this.lumiChunk = lumiChunk;
        this.root = root;

        this.chunkPosX = lumiChunk.lumi$chunkPosX();
        this.chunkPosZ = lumiChunk.lumi$chunkPosZ();
        this.skyLightHeightMap = skyLightHeightMap;
        this.outdatedSkylightColumns = outdatedSkylightColumns;

        this.minSkyLightHeight = Integer.MAX_VALUE;
        this.queuedRandomLightUpdates = 0;
        this.isFullyLit = false;
    }

    @Override
    public @NotNull RPLEChunkRoot lumi$root() {
        return root;
    }

    @Override
    public @NotNull RPLEWorld lumi$world() {
        return world;
    }

    @Override
    public @NotNull String lumi$chunkID() {
        return chunkID;
    }

    @Override
    public void lumi$writeToNBT(@NotNull NBTTagCompound output) {
        output.setIntArray(SKY_LIGHT_HEIGHT_MAP_NBT_TAG_NAME, skyLightHeightMap);
        output.setBoolean(IS_LIGHT_INITIALIZED_NBT_TAG_NAME, isFullyLit);
    }

    @Override
    public void lumi$readFromNBT(@NotNull NBTTagCompound input) {
        isFullyLit = false;
        skyLightHeightMapValidCheck:
        {
            if (!input.hasKey(IS_LIGHT_INITIALIZED_NBT_TAG_NAME, 1))
                break skyLightHeightMapValidCheck;
            val isLightInitializedInput = input.getBoolean(IS_LIGHT_INITIALIZED_NBT_TAG_NAME);
            if (!isLightInitializedInput)
                break skyLightHeightMapValidCheck;

            if (!input.hasKey(SKY_LIGHT_HEIGHT_MAP_NBT_TAG_NAME, 11))
                break skyLightHeightMapValidCheck;
            val skyLightHeightMapInput = input.getIntArray(SKY_LIGHT_HEIGHT_MAP_NBT_TAG_NAME);
            if (skyLightHeightMapInput.length != HEIGHT_MAP_ARRAY_SIZE)
                break skyLightHeightMapValidCheck;

            System.arraycopy(skyLightHeightMapInput, 0, skyLightHeightMap, 0, HEIGHT_MAP_ARRAY_SIZE);
            isFullyLit = true;
        }
        if (!isFullyLit)
            world.lumi$lightingEngine().handleChunkInit(this);
    }

    @Override
    public void lumi$writeToPacket(@NotNull ByteBuffer output) {
    }

    @Override
    public void lumi$readFromPacket(@NotNull ByteBuffer input) {
        isFullyLit = true;
    }

    @Override
    public @Nullable RPLESubChunk lumi$getSubChunkIfPrepared(int chunkPosY) {
        val lumiSubChunk = lumiChunk.lumi$getSubChunkIfPrepared(chunkPosY);
        if (!(lumiSubChunk instanceof RPLESubChunkRoot))
            return null;
        val subChunkRoot = (RPLESubChunkRoot) lumiSubChunk;
        return subChunkRoot.rple$subChunk(channel);
    }

    @Override
    public @NotNull RPLESubChunk lumi$getSubChunk(int chunkPosY) {
        val lumiSubChunk = lumiChunk.lumi$getSubChunk(chunkPosY);
        val subChunkRoot = (RPLESubChunkRoot) lumiSubChunk;
        return subChunkRoot.rple$subChunk(channel);
    }

    @Override
    public int lumi$chunkPosX() {
        return chunkPosX;
    }

    @Override
    public int lumi$chunkPosZ() {
        return chunkPosZ;
    }

    @Override
    public void lumi$queuedRandomLightUpdates(int queuedRandomLightUpdates) {
        this.queuedRandomLightUpdates = queuedRandomLightUpdates;
    }

    @Override
    public int lumi$queuedRandomLightUpdates() {
        return queuedRandomLightUpdates;
    }

    @Override
    public void lumi$resetQueuedRandomLightUpdates() {
        queuedRandomLightUpdates = 0;
    }

    @Override
    public int lumi$getBrightness(@NotNull LightType lightType,
                                  int subChunkPosX,
                                  int posY,
                                  int subChunkPosZ) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                return lumi$getBrightness(subChunkPosX, posY, subChunkPosZ);
            case SKY_LIGHT_TYPE:
                return lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
            default:
                return 0;
        }
    }

    @Override
    public int lumi$getBrightness(int subChunkPosX, int posY, int subChunkPosZ) {
        val blockBrightness = lumi$getBlockBrightness(subChunkPosX, posY, subChunkPosZ);
        val blockLightValue = lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
        return Math.max(blockBrightness, blockLightValue);
    }

    @Override
    public int lumi$getLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        val blockLightValue = lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
        val skyLightValue = lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
        return Math.max(blockLightValue, skyLightValue);
    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType,
                                   int subChunkPosX,
                                   int posY,
                                   int subChunkPosZ,
                                   int lightValue) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                lumi$setBlockLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
                break;
            case SKY_LIGHT_TYPE:
                lumi$setSkyLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
                break;
            default:
                break;
        }
    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int subChunkPosX, int posY, int subChunkPosZ) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                return lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
            case SKY_LIGHT_TYPE:
                return lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
            default:
                return 0;
        }
    }

    @Override
    public void lumi$setBlockLightValue(int subChunkPosX, int posY, int subChunkPosZ, int lightValue) {
        val chunkPosY = (posY & 255) / 16;

        subChunkPosX &= 15;
        val subChunkPosY = posY & 15;
        subChunkPosZ &= 15;

        val subChunk = lumi$getSubChunk(chunkPosY);
        subChunk.lumi$setBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);

        root.lumi$markDirty();
    }

    @Override
    public int lumi$getBlockLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkPosY = (posY & 255) / 16;

        val subChunk = lumi$getSubChunkIfPrepared(chunkPosY);
        if (subChunk == null)
            return BLOCK_LIGHT_TYPE.defaultLightValue();

        subChunkPosX &= 15;
        val subChunkPosY = posY & 15;
        subChunkPosZ &= 15;

        return subChunk.lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public void lumi$setSkyLightValue(int subChunkPosX, int posY, int subChunkPosZ, int lightValue) {
        if (!world.lumi$root().lumi$hasSky())
            return;

        val chunkPosY = (posY & 255) / 16;

        subChunkPosX &= 15;
        val subChunkPosY = posY & 15;
        subChunkPosZ &= 15;

        val subChunk = lumi$getSubChunk(chunkPosY);
        subChunk.lumi$setSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);

        root.lumi$markDirty();
    }

    @Override
    public int lumi$getSkyLightValue(int subChunkPosX, int posY, int subChunkPosZ) {
        if (!world.lumi$root().lumi$hasSky())
            return 0;

        val chunkPosY = (posY & 255) >> 4;

        subChunkPosX &= 15;
        subChunkPosZ &= 15;

        val subChunk = lumi$getSubChunkIfPrepared(chunkPosY);
        if (subChunk == null) {
            if (lumi$canBlockSeeSky(subChunkPosX, posY, subChunkPosZ))
                return SKY_LIGHT_TYPE.defaultLightValue();
            return 0;
        }

        val subChunkPosY = posY & 15;
        return subChunk.lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public int lumi$getBlockBrightness(int subChunkPosX, int posY, int subChunkPosZ) {
        val block = root.lumi$getBlock(subChunkPosX, posY, subChunkPosZ);
        val blockMeta = root.lumi$getBlockMeta(subChunkPosX, posY, subChunkPosZ);
        return lumi$getBlockBrightness(block, blockMeta, subChunkPosX, posY, subChunkPosZ);
    }

    @Override
    public int lumi$getBlockOpacity(int subChunkPosX, int posY, int subChunkPosZ) {
        val block = root.lumi$getBlock(subChunkPosX, posY, subChunkPosZ);
        val blockMeta = root.lumi$getBlockMeta(subChunkPosX, posY, subChunkPosZ);
        return lumi$getBlockOpacity(block, blockMeta, subChunkPosX, posY, subChunkPosZ);
    }

    @Override
    public int lumi$getBlockBrightness(@NotNull Block block,
                                       int blockMeta,
                                       int subChunkPosX,
                                       int posY,
                                       int subChunkPosZ) {
        val posX = (chunkPosX << 4) + subChunkPosX;
        val posZ = (chunkPosZ << 4) + subChunkPosZ;
        return world.lumi$getBlockBrightness(block, blockMeta, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(@NotNull Block block,
                                    int blockMeta,
                                    int subChunkPosX,
                                    int posY,
                                    int subChunkPosZ) {
        val posX = (chunkPosX << 4) + subChunkPosX;
        val posZ = (chunkPosZ << 4) + subChunkPosZ;
        return world.lumi$getBlockOpacity(block, blockMeta, posX, posY, posZ);
    }

    @Override
    public boolean lumi$canBlockSeeSky(int subChunkPosX, int posY, int subChunkPosZ) {
        subChunkPosX &= 15;
        subChunkPosZ &= 15;
        val index = subChunkPosX + (subChunkPosZ << 4);
        val maxPosY = skyLightHeightMap[index];
        return maxPosY <= posY;
    }

    @Override
    public void lumi$skyLightHeight(int subChunkPosX, int subChunkPosZ, int skyLightHeight) {
        subChunkPosX &= 15;
        subChunkPosZ &= 15;
        val index = subChunkPosX + (subChunkPosZ << 4);
        skyLightHeightMap[index] = skyLightHeight;
    }

    @Override
    public int lumi$skyLightHeight(int subChunkPosX, int subChunkPosZ) {
        subChunkPosX &= 15;
        subChunkPosZ &= 15;
        val index = subChunkPosX + (subChunkPosZ << 4);
        return skyLightHeightMap[index];
    }

    @Override
    public void lumi$minSkyLightHeight(int minSkyLightHeight) {
        this.minSkyLightHeight = minSkyLightHeight;
    }

    @Override
    public int lumi$minSkyLightHeight() {
        return minSkyLightHeight;
    }

    @Override
    public void lumi$isSkyLightHeightMapValid(boolean isSkyLightHeightMapValid) {
        this.isSkyLightHeightMapValid = isSkyLightHeightMapValid;
    }

    @Override
    public boolean lumi$isSkyLightHeightMapValid() {
        return isSkyLightHeightMapValid;
    }

    @Override
    public void lumi$isHeightOutdated(int subChunkPosX, int subChunkPosZ, boolean isHeightOutdated) {
        subChunkPosX &= 15;
        subChunkPosZ &= 15;
        val index = subChunkPosX + (subChunkPosZ << 4);
        outdatedSkylightColumns[index] = isHeightOutdated;
    }

    @Override
    public boolean lumi$isHeightOutdated(int subChunkPosX, int subChunkPosZ) {
        subChunkPosX &= 15;
        subChunkPosZ &= 15;
        val index = subChunkPosX + (subChunkPosZ << 4);
        return outdatedSkylightColumns[index];
    }

    @Override
    public void lumi$resetOutdatedHeightFlags() {
        Arrays.fill(outdatedSkylightColumns, true);
    }

    @Override
    public void lumi$isFullyLit(boolean isFullyLit) {
        this.isFullyLit = isFullyLit;
    }

    @Override
    public boolean lumi$isFullyLit() {
        return isFullyLit;
    }
}
