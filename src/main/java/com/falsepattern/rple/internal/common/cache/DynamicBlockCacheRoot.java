package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.Getter;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.BitSet;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.lumina.api.lighting.LightType.SKY_LIGHT_TYPE;
import static com.falsepattern.rple.internal.common.color.ColorPackingUtil.*;
import static net.minecraftforge.common.util.ForgeDirection.*;


//TODO: [CACHE_DONE] When a light color value is requested, it will get it from the requested block
//TODO: [CACHE_DONE] Said value MUST NOT be stored as the provided object, and must be IMMEDIATELY unpacked into the integer RGB values
//TODO: [CACHE_DONE] This is because there are ZERO promises on the value being immutable
//
//TODO: [CACHE_DONE] If a new value is queried and provided by any of the non-root caches, it must be propagated to ALL other caches
//TODO: [CACHE_DONE] This is VERY IMPORTANT! as getting the light/opacity value FROM is expensive!
public final class DynamicBlockCacheRoot implements RPLEBlockCacheRoot {
    static final int CHUNK_XZ_SIZE = 16;
    static final int CHUNK_XZ_BITMASK = 15;
    static final int CHUNK_Y_SIZE = 256;
    static final int CHUNK_Y_BITMASK = 255;
    static final int CACHE_CHUNK_XZ_SIZE = 3;
    static final int CENTER_TO_MIN_DISTANCE = CACHE_CHUNK_XZ_SIZE / 2;
    static final int TOTAL_CACHED_CHUNK_COUNT = CACHE_CHUNK_XZ_SIZE * CACHE_CHUNK_XZ_SIZE;
    static final int ELEMENT_COUNT_PER_CHUNK = CHUNK_XZ_SIZE * CHUNK_XZ_SIZE * CHUNK_Y_SIZE;
    static final int ELEMENT_COUNT_PER_CACHED_THING = TOTAL_CACHED_CHUNK_COUNT * ELEMENT_COUNT_PER_CHUNK;

    static final int BITSIZE_CHUNK_XZ = 4;
    static final int BITSIZE_CHUNK_Y = 8;
    static final int BITSHIFT_CHUNK_Z = BITSIZE_CHUNK_XZ + BITSIZE_CHUNK_Y;
    static final int BITSHIFT_CHUNK_X = BITSIZE_CHUNK_Y;
    static final int BITSHIFT_CHUNK = BITSIZE_CHUNK_XZ + BITSIZE_CHUNK_XZ + BITSIZE_CHUNK_Y;

    static final int COLOR_CHANNEL_COUNT = ColorChannel.values().length;

    private final RPLEWorldRoot worldRoot;

    private final DynamicBlockCache[] blockCaches = new DynamicBlockCache[COLOR_CHANNEL_COUNT];

    // Z/X 3/3
    private final RPLEChunkRoot[] rootChunks = new RPLEChunkRoot[TOTAL_CACHED_CHUNK_COUNT];
    // Used for populating
    private final ChunkCacheCompact helperCache = new ChunkCacheCompact();

    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final Block[] blocks = new Block[ELEMENT_COUNT_PER_CACHED_THING];
    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final int[] blockMetas = new int[ELEMENT_COUNT_PER_CACHED_THING];
    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final BitSet airChecks = new BitSet(ELEMENT_COUNT_PER_CACHED_THING);

    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final BitSet checkedBlocks = new BitSet(ELEMENT_COUNT_PER_CACHED_THING);

    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final short[] blockLightValues = new short[ELEMENT_COUNT_PER_CACHED_THING];
    // CZ/CX/Z/X/Y 3/3/16/16/256
    private final short[] blockOpacityValues = new short[ELEMENT_COUNT_PER_CACHED_THING];

    
    private int minChunkPosX;
    private int minChunkPosZ;
    private int maxChunkPosX;
    private int maxChunkPosZ;

    @Getter
    private boolean isReady;

    public DynamicBlockCacheRoot(@NotNull RPLEWorldRoot worldRoot) {
        this.worldRoot = worldRoot;
    }

    @Override
    public @NotNull String lumi$blockCacheRootID() {
        return "rple_dynamic_block_cache_root";
    }

    @Override
    public @NotNull RPLEBlockCache lumi$createBlockCache(LumiWorld world) {
        if (!(world instanceof RPLEWorld))
            throw new IllegalArgumentException("World must be an RPLEWorld");
        val rpleWorld = (RPLEWorld) world;
        val channel = rpleWorld.rple$channel();
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            blockCaches[cacheIndex] = new DynamicBlockCache(rpleWorld);
        else if (blockCaches[cacheIndex].lumi$world() != world)
            throw new IllegalArgumentException("Block cache already created for a different world");

        return blockCaches[cacheIndex];
    }

    @Override
    public int lumi$minChunkPosX() {
        return minChunkPosX;
    }

    @Override
    public int lumi$minChunkPosZ() {
        return minChunkPosZ;
    }

    @Override
    public int lumi$maxChunkPosX() {
        return maxChunkPosX;
    }

    @Override
    public int lumi$maxChunkPosZ() {
        return maxChunkPosZ;
    }

    @Override
    public void lumi$clearCache() {
        if (!isReady)
            return;

        // We don't need to clear the "blocks" array because blocks are singletons
        Arrays.fill(rootChunks, null);
        checkedBlocks.clear();

        isReady = false;
    }

    @Override
    public @NotNull String lumi$blockStorageRootID() {
        return "rple_dynamic_block_cache_root";
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
        return blocks[getIndex(posX, posY, posZ)];
    }

    @Override
    public int lumi$getBlockMeta(int posX, int posY, int posZ) {
        return blockMetas[getIndex(posX, posY, posZ)];
    }

    @Override
    public boolean lumi$isAirBlock(int posX, int posY, int posZ) {
        return airChecks.get(getIndex(posX, posY, posZ));
    }

    @Override
    public @Nullable TileEntity lumi$getTileEntity(int posX, int posY, int posZ) {
        val block = lumi$getBlock(posX, posY, posZ);
        val blockMeta = lumi$getBlockMeta(posX, posY, posZ);
        if (!block.hasTileEntity(blockMeta))
            return null;
        val chunkRoot = chunkFromBlockPos(posX, posZ);
        if (chunkRoot == null)
            return null;

        val chunkBase = (Chunk) chunkRoot;
        val subChunkX = posX & CHUNK_XZ_BITMASK;
        val subChunkZ = posZ & CHUNK_XZ_BITMASK;
        return chunkBase.getTileEntityUnsafe(subChunkX, posY, subChunkZ);
    }

    @Override
    public @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel) {
        val cacheIndex = channel.ordinal();
        if (blockCaches[cacheIndex] == null)
            throw new IllegalStateException("Block cache not created for channel " + channel.name());
        return blockCaches[cacheIndex];
    }

    @Override
    public @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel) {
        return rple$blockCache(channel);
    }

    private int getIndex(int posX, int posY, int posZ) {
        val theChunk = chunkFromBlockPos(posX, posZ);
        val cacheIndex = cacheIndexFromBlockPos(posX, posY, posZ);
        if (checkedBlocks.get(cacheIndex))
            return cacheIndex;

        if (theChunk == null) {
            blocks[cacheIndex] = Blocks.air;
            blockMetas[cacheIndex] = 0;
            airChecks.set(cacheIndex);
            checkedBlocks.clear(cacheIndex);
            blockLightValues[cacheIndex] = 0;
            blockOpacityValues[cacheIndex] = 0;
        } else {
            val subChunkX = posX & CHUNK_XZ_BITMASK;
            val subChunkZ = posZ & CHUNK_XZ_BITMASK;

            val block = theChunk.lumi$getBlock(subChunkX, posY, subChunkZ);
            blocks[cacheIndex] = block;

            if (block != Blocks.air) {
                val blockMeta = theChunk.lumi$getBlockMeta(subChunkX, posY, subChunkZ);

                airChecks.set(cacheIndex, block.isAir(helperCache, posX, posY, posZ));
                blockMetas[cacheIndex] = blockMeta;
                blockLightValues[cacheIndex] = ((RPLEBlock) block).rple$getRawBrightnessColor(helperCache, blockMeta, posX, posY, posZ);
                blockOpacityValues[cacheIndex] = ((RPLEBlock) block).rple$getRawOpacityColor(helperCache, blockMeta, posX, posY, posZ);
            } else {
                airChecks.set(cacheIndex);
                blockMetas[cacheIndex] = 0;
                blockLightValues[cacheIndex] = 0;
                blockOpacityValues[cacheIndex] = 0;
            }
            checkedBlocks.set(cacheIndex);
        }
        return cacheIndex;
    }

    private void setupCache(int centerChunkPosX, int centerChunkPosZ) {
        val minChunkPosX = centerChunkPosX - CENTER_TO_MIN_DISTANCE;
        val minChunkPosZ = centerChunkPosZ - CENTER_TO_MIN_DISTANCE;

        val maxChunkPosX = minChunkPosX + CACHE_CHUNK_XZ_SIZE;
        val maxChunkPosZ = minChunkPosZ + CACHE_CHUNK_XZ_SIZE;

        for (var chunkPosZ = 0; chunkPosZ < CACHE_CHUNK_XZ_SIZE; chunkPosZ++) {
            val realChunkPosZ = chunkPosZ + minChunkPosZ;
            for (var chunkPosX = 0; chunkPosX < CACHE_CHUNK_XZ_SIZE; chunkPosX++) {
                val rootChunkIndex = (chunkPosZ * CACHE_CHUNK_XZ_SIZE) + chunkPosX;
                val realChunkPosX = chunkPosX + minChunkPosX;
                rootChunks[rootChunkIndex] = worldRoot.rple$getChunkRootFromChunkPosIfExists(realChunkPosX,
                                                                                             realChunkPosZ);
            }
        }
        helperCache.init(rootChunks, CACHE_CHUNK_XZ_SIZE, minChunkPosX, minChunkPosZ);
        this.minChunkPosX = minChunkPosX;
        this.minChunkPosZ = minChunkPosZ;
        this.maxChunkPosX = maxChunkPosX;
        this.maxChunkPosZ = maxChunkPosZ;
        checkedBlocks.clear();
        isReady = true;
    }

    int cacheIndexFromBlockPos(int posX, int posY, int posZ) {
        val chunkPosZ = (posZ >> BITSIZE_CHUNK_XZ) - minChunkPosZ;
        val chunkPosX = (posX >> BITSIZE_CHUNK_XZ) - minChunkPosX;

        // val chunkBase = (chunkPosZ * CACHE_CHUNK_XZ_SIZE + chunkPosX) * ELEMENT_COUNT_PER_CHUNK;
        // chunk element count is always 16*16*256, so we optimize away the multiply
        val chunkBase = (chunkPosZ * CACHE_CHUNK_XZ_SIZE + chunkPosX) << BITSHIFT_CHUNK;

        val subChunkZ = posZ & CHUNK_XZ_BITMASK;
        val subChunkX = posX & CHUNK_XZ_BITMASK;
        val subChunkY = posY & CHUNK_Y_BITMASK;

        //val subChunkOffset = (subChunkZ * CHUNK_XZ_SIZE + subChunkX) * CHUNK_Y_SIZE + subChunkY;
        //All these are constants so we can reduce it to bit shuffling
        val subChunkOffset = (subChunkZ << BITSHIFT_CHUNK_Z) | (subChunkX << BITSHIFT_CHUNK_X) | subChunkY;
        int index = chunkBase | subChunkOffset;
        if (index < 0 || index >= blocks.length) {
            chunkFromBlockPos(posX, posZ);
            return cacheIndexFromBlockPos(posX, posY, posZ);
        } else {
            return index;
        }
    }

    private @Nullable RPLEChunkRoot chunkFromBlockPos(int posX, int posZ) {
        val baseChunkPosX = posX >> BITSIZE_CHUNK_XZ;
        val baseChunkPosZ = posZ >> BITSIZE_CHUNK_XZ;
        if (!isReady) {
            setupCache(baseChunkPosX, baseChunkPosZ);
        }

        if (baseChunkPosX < minChunkPosX || baseChunkPosX >= maxChunkPosX ||
            baseChunkPosZ < minChunkPosZ || baseChunkPosZ >= maxChunkPosZ) {
            setupCache(baseChunkPosX, baseChunkPosZ);
        }
        val chunkPosX = baseChunkPosX - minChunkPosX;
        val chunkPosZ = baseChunkPosZ - minChunkPosZ;

        val rootChunk = rootChunks[chunkPosZ * CACHE_CHUNK_XZ_SIZE + chunkPosX];
        if (rootChunk != null)
            return rootChunk;

        return rootChunks[chunkPosZ * CACHE_CHUNK_XZ_SIZE + chunkPosX] =
                worldRoot.rple$getChunkRootFromChunkPosIfExists(baseChunkPosX,
                                                                baseChunkPosZ);
    }

    //TODO: [CACHE_DONE] Will store one brightness/opacity per block, per channel
    //TODO: [CACHE_DONE] Unlike LUMINA, it will ask the root for said values instead of getting them on it's own
    public final class DynamicBlockCache implements RPLEBlockCache {
        private final RPLEWorld world;
        private final int cacheEntryShift;

        public DynamicBlockCache(@NotNull RPLEWorld world) {
            this.world = world;
            switch (world.rple$channel()) {
                default:
                case RED_CHANNEL:
                    cacheEntryShift = CACHE_ENTRY_RED_OFFSET;
                    break;
                case GREEN_CHANNEL:
                    cacheEntryShift = CACHE_ENTRY_GREEN_OFFSET;
                    break;
                case BLUE_CHANNEL:
                    cacheEntryShift = CACHE_ENTRY_BLUE_OFFSET;
                    break;
            }
        }

        @Override
        public @NotNull ColorChannel rple$channel() {
            return world.rple$channel();
        }

        @Override
        public @NotNull RPLEBlockCacheRoot lumi$root() {
            return DynamicBlockCacheRoot.this;
        }

        @Override
        public @NotNull String lumi$BlockCacheID() {
            return "rple_dynamic_block_cache";
        }

        @Override
        public void lumi$clearCache() {
        }

        @Override
        public @NotNull String lumi$blockStorageID() {
            return "rple_dynamic_block_cache";
        }

        @Override
        public @NotNull RPLEWorld lumi$world() {
            return world;
        }

        @Override
        public int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
            // TODO: [CACHE] Equal implementation to one found in RPLEWorldContainer, optionally cached.
            //  FP: Will do once the serverside works
            var blockLightValue = rple$getChannelLightValueForRender(BLOCK_LIGHT_TYPE, posX, posY, posZ);
            blockLightValue = Math.max(blockLightValue, minBlockLight);
            val skyLightValue = rple$getChannelLightValueForRender(SKY_LIGHT_TYPE, posX, posY, posZ);
            return TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(blockLightValue, skyLightValue);
        }

        @Override
        public int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
            // TODO: [CACHE] Equal implementation to one found in RPLEWorldContainer, optionally cached.
            //  FP: Will do once the serverside works
            if (lightType == SKY_LIGHT_TYPE && !DynamicBlockCacheRoot.this.lumi$hasSky())
                return 0;

            if (posY < 0) {
                posY = 0;
            } else if (posY > 255) {
                return lightType.defaultLightValue();
            }
            if (posX < -30000000 || posX >= 30000000)
                return lightType.defaultLightValue();
            if (posZ < -30000000 || posZ >= 30000000)
                return lightType.defaultLightValue();

            val block = DynamicBlockCacheRoot.this.lumi$getBlock(posX, posY, posZ);
            if (block.getUseNeighborBrightness()) {
                var lightValue = 0;
                lightValue = Math.max(lightValue, getNeighborLightValue(lightType, posX, posY, posZ, UP));
                lightValue = Math.max(lightValue, getNeighborLightValue(lightType, posX, posY, posZ, NORTH));
                lightValue = Math.max(lightValue, getNeighborLightValue(lightType, posX, posY, posZ, SOUTH));
                lightValue = Math.max(lightValue, getNeighborLightValue(lightType, posX, posY, posZ, WEST));
                lightValue = Math.max(lightValue, getNeighborLightValue(lightType, posX, posY, posZ, EAST));
                return lightValue;
            }
            return lumi$getBrightness(lightType, posX, posY, posZ);
        }

        private int getNeighborLightValue(LightType lightType, int posX, int posY, int posZ, ForgeDirection direction) {
            posX += direction.offsetX;
            posY += direction.offsetY;
            posZ += direction.offsetZ;
            return lumi$getBrightness(lightType, posX, posY, posZ);
        }

        @Override
        public int lumi$getBrightness(@NotNull LightType lightType, int posX, int posY, int posZ) {
            switch (lightType) {
                case BLOCK_LIGHT_TYPE:
                    return lumi$getBrightness(posX, posY, posZ);
                case SKY_LIGHT_TYPE:
                    return lumi$getSkyLightValue(posX, posY, posZ);
                default:
                    return 0;
            }
        }

        @Override
        public int lumi$getBrightness(int posX, int posY, int posZ) {
            val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
            if (chunk != null) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return chunk.lumi$getBrightness(subChunkPosX, posY, subChunkPosZ);
            }
            val blockBrightness = lumi$getBlockBrightness(posX, posY, posZ);
            return Math.max(blockBrightness, BLOCK_LIGHT_TYPE.defaultLightValue());
        }

        @Override
        public int lumi$getLightValue(int posX, int posY, int posZ) {
            val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
            if (chunk != null) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return chunk.lumi$getLightValue(subChunkPosX, posY, subChunkPosZ);
            }
            return LightType.maxBaseLightValue();
        }

        @Override
        public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
            val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
            if (chunk != null) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return chunk.lumi$getLightValue(lightType, subChunkPosX, posY, subChunkPosZ);
            }

            switch (lightType) {
                default:
                case BLOCK_LIGHT_TYPE:
                    return BLOCK_LIGHT_TYPE.defaultLightValue();
                case SKY_LIGHT_TYPE: {
                    if (lumi$root().lumi$hasSky())
                        return SKY_LIGHT_TYPE.defaultLightValue();
                    return 0;
                }
            }
        }

        @Override
        public int lumi$getBlockLightValue(int posX, int posY, int posZ) {
            val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
            if (chunk != null) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return chunk.lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
            }
            return BLOCK_LIGHT_TYPE.defaultLightValue();
        }

        @Override
        public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
            if (!lumi$root().lumi$hasSky())
                return 0;

            val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
            if (chunk != null) {
                val subChunkPosX = posX & 15;
                val subChunkPosZ = posZ & 15;
                return chunk.lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
            }

            return SKY_LIGHT_TYPE.defaultLightValue();
        }

        @Override
        public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
            val index = DynamicBlockCacheRoot.this.getIndex(posX, posY, posZ);
            short cachedBrightness = blockLightValues[index];
            return cacheToChannel(cachedBrightness, cacheEntryShift);
        }

        @Override
        public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
            val index = DynamicBlockCacheRoot.this.getIndex(posX, posY, posZ);
            short cachedOpacity = blockOpacityValues[index];
            return cacheToChannel(cachedOpacity, cacheEntryShift);
        }

        @Override
        public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            return lumi$getBlockBrightness(posX, posY, posZ);
        }

        @Override
        public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
            return lumi$getBlockOpacity(posX, posY, posZ);
        }

        private @Nullable LumiChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ) {
            val chunkRoot = chunkFromBlockPos(posX, posZ);
            if (chunkRoot == null)
                return null;
            return chunkRoot.rple$chunk(world.rple$channel());
        }
    }

}
