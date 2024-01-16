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

package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.cache.LumiBlockCacheRoot;
import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.falsepattern.rple.internal.common.cache.BlockCaches.createFallbackBlockCacheRoot;
import static com.falsepattern.rple.internal.common.cache.DynamicBlockCacheRoot.COLOR_CHANNEL_COUNT;

// TODO: [CACHE_DONE] Initial implementation
// TODO: [CACHE_CLEANUP] May be abstracted once the system is initialized through managers
public final class MultiHeadBlockCacheRoot implements RPLEBlockCacheRoot {
    public static final int MAX_MULTI_HEAD_CACHE_COUNT = 8;
    private final int multiHeadCacheCount;

    private final MultiHeadBlockCache[] blockCaches = new MultiHeadBlockCache[COLOR_CHANNEL_COUNT];

    private final RPLEBlockCacheRoot fallback;
    private final DynamicBlockCacheRoot[] cacheRoots;
    private final Thread ownerThread;
    private final LRU lru;

    public MultiHeadBlockCacheRoot(@NotNull RPLEWorldRoot worldRoot, int multiHeadCacheCount) {
        multiHeadCacheCount = Math.min(Math.max(multiHeadCacheCount, 1), MAX_MULTI_HEAD_CACHE_COUNT);
        this.multiHeadCacheCount = multiHeadCacheCount;
        lru = new LRU();
        cacheRoots = new DynamicBlockCacheRoot[multiHeadCacheCount];
        ownerThread = Thread.currentThread();
        for (int i = 0; i < multiHeadCacheCount; i++)
            cacheRoots[i] = new DynamicBlockCacheRoot(worldRoot);
        fallback = createFallbackBlockCacheRoot(worldRoot);
    }

    @Override
    public @NotNull String lumi$blockCacheRootID() {
        return "rple_multihead_dynamic_block_cache_root";
    }

    @Override
    public @NotNull MultiHeadBlockCache lumi$createBlockCache(LumiWorld world) {
        if (!(world instanceof RPLEWorld))
            throw new IllegalArgumentException("World must be an RPLEWorld");
        val rpleWorld = (RPLEWorld) world;
        val channel = rpleWorld.rple$channel();
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            blockCaches[cacheIndex] = new MultiHeadBlockCache(rpleWorld);
        else if (blockCaches[cacheIndex].lumi$world() != world)
            throw new IllegalArgumentException("Block cache already created for a different world");

        return blockCaches[cacheIndex];
    }

    @Override
    public void lumi$prefetchChunk(@Nullable LumiChunk chunk) {}

    @Override
    public @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel) {
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            throw new IllegalStateException("Block cache not created for channel " + channel.name());
        return blockCaches[cacheIndex];
    }

    @Override
    public void lumi$clearCache() {
        for (var i = 0; i < multiHeadCacheCount; i++)
            cacheRoots[i].lumi$clearCache();
    }

    @Override
    public @NotNull String lumi$blockStorageRootID() {
        return "rple_multihead_dynamic_block_cache_root";
    }

    @Override
    public boolean lumi$isClientSide() {
        return fallback.lumi$isClientSide();
    }

    @Override
    public boolean lumi$hasSky() {
        return fallback.lumi$hasSky();
    }

    @Override
    public @NotNull Block lumi$getBlock(int posX, int posY, int posZ) {
        val bestCache = getCacheRoot(posX >> 4, posZ >> 4);
        return bestCache.lumi$getBlock(posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockMeta(int posX, int posY, int posZ) {
        val bestCache = getCacheRoot(posX >> 4, posZ >> 4);
        return bestCache.lumi$getBlockMeta(posX, posY, posZ);
    }

    @Override
    public boolean lumi$isAirBlock(int posX, int posY, int posZ) {
        val bestCache = getCacheRoot(posX >> 4, posZ >> 4);
        return bestCache.lumi$isAirBlock(posX, posY, posZ);
    }

    @Override
    public @Nullable TileEntity lumi$getTileEntity(int posX, int posY, int posZ) {
        return fallback.lumi$getTileEntity(posX, posY, posZ);
    }

    @Override
    public @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel) {
        return rple$blockCache(channel);
    }

    private static boolean inRange(DynamicBlockCacheRoot cache, int chunkPosX, int chunkPosZ) {
        return cache.minChunkPosX() <= chunkPosX &&
               cache.maxChunkPosX() >= chunkPosX &&
               cache.minChunkPosZ() <= chunkPosZ &&
               cache.maxChunkPosZ() >= chunkPosZ;
    }

    private LumiBlockCacheRoot getCacheRoot(int chunkPosX, int chunkPosZ) {
        if (Thread.currentThread() != ownerThread)
            return fallback;
        for (int i = 0; i < multiHeadCacheCount; i++) {
            if (inRange(cacheRoots[i], chunkPosX, chunkPosZ)) {
                lru.update(i);
                return cacheRoots[i];
            }
        }
        int last = lru.last();
        return cacheRoots[last];
    }

    public final class MultiHeadBlockCache implements RPLEBlockCache {
        private final RPLEBlockCache fallback;
        private final DynamicBlockCacheRoot.DynamicBlockCache[] caches = new DynamicBlockCacheRoot.DynamicBlockCache[multiHeadCacheCount];

        private MultiHeadBlockCache(@NotNull RPLEWorld world) {
            for (var i = 0; i < multiHeadCacheCount; i++)
                caches[i] = cacheRoots[i].lumi$createBlockCache(world);
            fallback = MultiHeadBlockCacheRoot.this.fallback.lumi$createBlockCache(world);
        }

        @Override
        public @NotNull ColorChannel rple$channel() {
            return fallback.rple$channel();
        }

        @Override
        public @NotNull MultiHeadBlockCacheRoot lumi$root() {
            return MultiHeadBlockCacheRoot.this;
        }

        @Override
        public @NotNull String lumi$blockStorageID() {
            return "rple_multihead_dynamic_block_cache";
        }

        @Override
        public @NotNull RPLEWorld lumi$world() {
            return fallback.lumi$world();
        }

        @Override
        public int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
            // TODO: [CACHE] Equal implementation to one found in RPLEWorldContainer, optionally cached.
            //  FP: Will do once the serverside works
            return fallback.rple$getChannelBrightnessForTessellator(posX, posY, posZ, minBlockLight);
        }

        @Override
        public int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
            // TODO: [CACHE] Equal implementation to one found in RPLEWorldContainer, optionally cached.
            //  FP: Will do once the serverside works
            return fallback.rple$getChannelLightValueForRender(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBrightness(@NotNull LightType lightType, int posX, int posY, int posZ) {
            return fallback.lumi$getBrightness(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBrightness(int posX, int posY, int posZ) {
            return fallback.lumi$getBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(int posX, int posY, int posZ) {
            return fallback.lumi$getLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
            return fallback.lumi$getLightValue(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockLightValue(int posX, int posY, int posZ) {
            return fallback.lumi$getBlockLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
            return fallback.lumi$getSkyLightValue(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
            return getCache(posX >> 4, posZ >> 4).lumi$getBlockBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
            return getCache(posX >> 4, posZ >> 4).lumi$getBlockOpacity(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            return getCache(posX >> 4, posZ >> 4).lumi$getBlockBrightness(block, blockMeta, posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            return getCache(posX >> 4, posZ >> 4).lumi$getBlockOpacity(block, blockMeta, posX, posY, posZ);
        }

        @Override
        public @NotNull String lumi$BlockCacheID() {
            return "rple_multihead_dynamic_block_cache";
        }

        @Override
        public void lumi$clearCache() {
            for (var i = 0; i < multiHeadCacheCount; i++)
                caches[i].lumi$clearCache();
        }

        private RPLEBlockCache getCache(int chunkPosX, int chunkPosZ) {
            if (Thread.currentThread() != ownerThread)
                return fallback;
            for (var i = 0; i < multiHeadCacheCount; i++) {
                if (inRange(caches[i].lumi$root(), chunkPosX, chunkPosZ)) {
                    lru.update(i);
                    return caches[i];
                }
            }
            return caches[lru.last()];
        }
    }

    private class LRU {
        private final int[] keys;

        private LRU() {
            this.keys = new int[multiHeadCacheCount];
            for (int i = 0; i < multiHeadCacheCount; i++) {
                keys[i] = i;
            }
        }

        public synchronized void update(int value) {
            for (var i = 0; i < multiHeadCacheCount; i++) {
                if (keys[i] != value)
                    continue;
                if (i != 0) {
                    shiftRight(i);
                    keys[0] = value;
                }
                return;
            }
            throw new IllegalStateException("Illegal state in LRU cache. Keys: " + Arrays.toString(keys) + ", value: " + value + ".");
        }

        public synchronized int last() {
            val last = keys[multiHeadCacheCount - 1];
            shiftRight(multiHeadCacheCount - 1);
            keys[0] = last;
            return last;
        }

        private void shiftRight(int start) {
            for (var i = start; i > 0; i--)
                keys[i] = keys[i - 1];
        }
    }
}
