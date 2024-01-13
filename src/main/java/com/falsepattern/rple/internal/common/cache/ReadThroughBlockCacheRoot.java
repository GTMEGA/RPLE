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
public class ReadThroughBlockCacheRoot implements RPLEBlockCacheRoot {
    private final RPLEWorldRoot worldRoot;

    private final ReadThroughBlockCache[] blockCaches = new ReadThroughBlockCache[COLOR_CHANNEL_COUNT];

    @Override
    public @NotNull String lumi$blockCacheRootID() {
        return "rple_readthrough_block_cache_root";
    }

    @Override
    public @NotNull ReadThroughBlockCache lumi$createBlockCache(LumiWorld world) {
        if (!(world instanceof RPLEWorld))
            throw new IllegalArgumentException("World must be an RPLEWorld");
        val rpleWorld = (RPLEWorld) world;
        val channel = rpleWorld.rple$channel();
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            blockCaches[cacheIndex] = new ReadThroughBlockCache(rpleWorld);
        else if (blockCaches[cacheIndex].lumi$world() != world)
            throw new IllegalArgumentException("Block cache already created for a different world");

        return blockCaches[cacheIndex];
    }

    @Override
    public void lumi$prefetchChunk(@Nullable LumiChunk chunk) {}

    @Override
    public void lumi$clearCache() {}

    @Override
    public @NotNull ReadThroughBlockCache rple$blockCache(@NotNull ColorChannel channel) {
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            throw new IllegalStateException("Block cache not created for channel " + channel.name());
        return blockCaches[cacheIndex];
    }

    @Override
    public @NotNull ReadThroughBlockCache rple$blockStorage(@NotNull ColorChannel channel) {
        return rple$blockCache(channel);
    }

    @Override
    public @NotNull String lumi$blockStorageRootID() {
        return "rple_readthrough_block_cache_root";
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final class ReadThroughBlockCache implements RPLEBlockCache {
        private final RPLEWorld world;

        @Override
        public @NotNull ColorChannel rple$channel() {
            return world.rple$channel();
        }

        @Override
        public @NotNull RPLEBlockCacheRoot lumi$root() {
            return ReadThroughBlockCacheRoot.this;
        }

        @Override
        public @NotNull String lumi$blockStorageID() {
            return "rple_readthrough_block_cache";
        }

        @Override
        public @NotNull RPLEWorld lumi$world() {
            return world;
        }

        @Override
        public int lumi$getBrightness(@NotNull LightType lightType, int posX, int posY, int posZ) {
            return world.lumi$getBrightness(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBrightness(int posX, int posY, int posZ) {
            return world.lumi$getBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(int posX, int posY, int posZ) {
            return world.lumi$getLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
            return world.lumi$getLightValue(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockLightValue(int posX, int posY, int posZ) {
            return world.lumi$getBlockLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
            return world.lumi$getSkyLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
            return world.lumi$getBlockBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
            return world.lumi$getBlockOpacity(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            return world.lumi$getBlockBrightness(block, blockMeta, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
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
