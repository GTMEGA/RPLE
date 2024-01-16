/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.internal.common.cache.DynamicBlockCacheRoot.COLOR_CHANNEL_COUNT;

@RequiredArgsConstructor
public class FocusedBlockCacheRoot implements RPLEBlockCacheRoot {
    private final RPLEWorldRoot worldRoot;

    private final FocusedBlockCache[] blockCaches = new FocusedBlockCache[COLOR_CHANNEL_COUNT];

    @Nullable
    private RPLEChunkRoot focusedChunkRoot;

    private int focusedMinPosX = 0;
    private int focusedMinPosZ = 0;
    private int focusedMaxPosX = 0;
    private int focusedMaxPosZ = 0;

    @Override
    public @NotNull String lumi$blockCacheRootID() {
        return "rple_focused_block_cache_root";
    }

    @Override
    public @NotNull RPLEBlockCache lumi$createBlockCache(LumiWorld world) {
        if (!(world instanceof RPLEWorld))
            throw new IllegalArgumentException("World must be an RPLEWorld");
        val rpleWorld = (RPLEWorld) world;
        val channel = rpleWorld.rple$channel();
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            blockCaches[cacheIndex] = new FocusedBlockCache(rpleWorld);
        else if (blockCaches[cacheIndex].lumi$world() != world)
            throw new IllegalArgumentException("Block cache already created for a different world");

        return blockCaches[cacheIndex];
    }

    @Override
    public void lumi$prefetchChunk(@Nullable LumiChunk lumiChunk) {
        if (lumiChunk instanceof RPLEChunk) {
            val chunk = (RPLEChunk) lumiChunk;
            val chunkRoot = chunk.lumi$root();
            if (chunkRoot == focusedChunkRoot)
                return;

            this.focusedChunkRoot = chunkRoot;

            this.blockCaches[0].focusedChunk = focusedChunkRoot.rple$chunk(ColorChannel.RED_CHANNEL);
            this.blockCaches[1].focusedChunk = focusedChunkRoot.rple$chunk(ColorChannel.GREEN_CHANNEL);
            this.blockCaches[2].focusedChunk = focusedChunkRoot.rple$chunk(ColorChannel.BLUE_CHANNEL);

            val chunkPosX = chunk.lumi$chunkPosX();
            val chunkPosZ = chunk.lumi$chunkPosZ();

            this.focusedMinPosX = chunkPosX << 4;
            this.focusedMinPosZ = chunkPosZ << 4;

            this.focusedMaxPosX = focusedMinPosX + 15;
            this.focusedMaxPosZ = focusedMinPosZ + 15;
        } else {
            this.focusedChunkRoot = null;

            this.blockCaches[0].focusedChunk = null;
            this.blockCaches[1].focusedChunk = null;
            this.blockCaches[2].focusedChunk = null;
        }
    }

    @Override
    public void lumi$clearCache() {
        this.focusedChunkRoot = null;

        this.blockCaches[0].focusedChunk = null;
        this.blockCaches[1].focusedChunk = null;
        this.blockCaches[2].focusedChunk = null;
    }

    @Override
    public @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel) {
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            throw new IllegalStateException("Block cache not created for channel " + channel.name());
        return blockCaches[cacheIndex];
    }

    @Override
    public @NotNull RPLEBlockCache rple$blockStorage(@NotNull ColorChannel channel) {
        return rple$blockCache(channel);
    }

    @Override
    public @NotNull String lumi$blockStorageRootID() {
        return "rple_focused_block_cache_root";
    }

    @Override
    public boolean lumi$isClientSide() {
        return worldRoot.lumi$isClientSide();
    }

    @Override
    public boolean lumi$hasSky() {
        return worldRoot.lumi$hasSky();
    }

    @Override
    public @NotNull Block lumi$getBlock(int posX, int posY, int posZ) {
        return worldRoot.lumi$getBlock(posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockMeta(int posX, int posY, int posZ) {
        return worldRoot.lumi$getBlockMeta(posX, posY, posZ);
    }

    @Override
    public boolean lumi$isAirBlock(int posX, int posY, int posZ) {
        return worldRoot.lumi$isAirBlock(posX, posY, posZ);
    }

    @Override
    public @Nullable TileEntity lumi$getTileEntity(int posX, int posY, int posZ) {
        return worldRoot.lumi$getTileEntity(posX, posY, posZ);
    }

    private boolean isInFocus(int posX, int posZ) {
        check:
        {
            if (focusedChunkRoot == null)
                break check;
            if (posX < focusedMinPosX || posX > focusedMaxPosX)
                break check;
            if (posZ < focusedMinPosZ || posZ > focusedMaxPosZ)
                break check;

            return true;
        }
        return attemptRefocus(posX, posZ);
    }

    private boolean attemptRefocus(int posX, int posZ) {
        val chunkRoot = worldRoot.rple$getChunkRootFromBlockPosIfExists(posX, posZ);
        if (chunkRoot == null)
            return false;

        val redChunk = chunkRoot.rple$chunk(ColorChannel.RED_CHANNEL);

        this.focusedChunkRoot = chunkRoot;

        this.blockCaches[0].focusedChunk = redChunk;
        this.blockCaches[1].focusedChunk = focusedChunkRoot.rple$chunk(ColorChannel.GREEN_CHANNEL);
        this.blockCaches[2].focusedChunk = focusedChunkRoot.rple$chunk(ColorChannel.BLUE_CHANNEL);

        val chunkPosX = redChunk.lumi$chunkPosX();
        val chunkPosZ = redChunk.lumi$chunkPosZ();

        this.focusedMinPosX = chunkPosX << 4;
        this.focusedMinPosZ = chunkPosZ << 4;

        this.focusedMaxPosX = focusedMinPosX + 15;
        this.focusedMaxPosZ = focusedMinPosZ + 15;

        return true;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class FocusedBlockCache implements RPLEBlockCache {
        private final RPLEWorld world;

        @Nullable
        private RPLEChunk focusedChunk;

        @Override
        public @NotNull ColorChannel rple$channel() {
            return world.rple$channel();
        }

        @Override
        public @NotNull RPLEBlockCacheRoot lumi$root() {
            return FocusedBlockCacheRoot.this;
        }

        @Override
        public @NotNull String lumi$blockStorageID() {
            return "rple_focused_block_cache";
        }

        @Override
        public @NotNull RPLEWorld lumi$world() {
            return world;
        }

        @Override
        public int lumi$getBrightness(@NotNull LightType lightType, int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getBrightness(lightType, subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getBrightness(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBrightness(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getBrightness(subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getLightValue(subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getLightValue(lightType, subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getLightValue(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockLightValue(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getBlockLightValue(subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getBlockLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getSkyLightValue(subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getSkyLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                posY &= 255;

                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;

                val block = focusedChunkRoot.lumi$getBlock(subChunkPosX, posY, subChunkPosZ);
                val blockMeta = focusedChunkRoot.lumi$getBlockMeta(subChunkPosX, posY, subChunkPosZ);
                return focusedChunk.lumi$getBlockBrightness(block, blockMeta, subChunkPosX, posY, subChunkPosZ);
            }
            return world.lumi$getBlockBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                posY &= 255;

                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;

                val block = focusedChunkRoot.lumi$getBlock(subChunkPosX, posY, subChunkPosZ);
                val blockMeta = focusedChunkRoot.lumi$getBlockMeta(subChunkPosX, posY, subChunkPosZ);
                return focusedChunk.lumi$getBlockOpacity(block, blockMeta, subChunkPosX, posY, subChunkPosZ);
            }
            return world.lumi$getBlockOpacity(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getBlockBrightness(block, blockMeta, subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getBlockBrightness(block, blockMeta, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            if (isInFocus(posX, posZ)) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return focusedChunk.lumi$getBlockOpacity(block, blockMeta, subChunkPosX, posY & 255, subChunkPosZ);
            }
            return world.lumi$getBlockOpacity(block, blockMeta, posX, posY, posZ);
        }

        @Override
        public int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
            return world.rple$getChannelBrightnessForTessellator(posX, posY, posZ, minBlockLight);
        }

        @Override
        public int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
            return world.rple$getChannelLightValueForRender(lightType, posX, posY, posZ);
        }

        @Override
        public @NotNull String lumi$BlockCacheID() {
            return "rple_readthrough_block_cache";
        }

        @Override
        public void lumi$clearCache() {

        }
    }
}
