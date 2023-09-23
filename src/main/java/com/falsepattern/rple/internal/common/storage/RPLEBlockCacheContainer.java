package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.lumina.api.lighting.LightType.SKY_LIGHT_TYPE;
import static net.minecraftforge.common.util.ForgeDirection.*;

public final class RPLEBlockCacheContainer implements RPLEBlockCache {
    private final ColorChannel channel;
    private final String blockCacheID;
    private final ChunkCache blockCacheBase;
    private final RPLEWorld world;
    private final RPLEBlockCacheRoot root;
    private final int chunkPosXOffset;
    private final int chunkPosZOffset;

    @Nullable
    private final RPLEChunk @Nullable [] @Nullable [] chunks;
    private final boolean isEmpty;

    @SuppressWarnings("InstanceofIncompatibleInterface")
    public RPLEBlockCacheContainer(@NotNull ColorChannel channel,
                                   @NotNull ChunkCache blockCacheBase,
                                   @NotNull RPLEWorldRoot worldRoot,
                                   @NotNull RPLEBlockCacheRoot root) {
        this.channel = channel;
        this.blockCacheID = Tags.MOD_ID + "_" + channel + "_block_cache";
        this.blockCacheBase = blockCacheBase;
        this.world = worldRoot.rple$world(channel);
        this.root = root;
        this.chunkPosXOffset = blockCacheBase.chunkX;
        this.chunkPosZOffset = blockCacheBase.chunkZ;

        val baseChunks = blockCacheBase.chunkArray;
        RPLEChunk[][] tempChunks = null;
        var tempIsEmpty = true;
        if (baseChunks != null) {
            val xChunksSize = baseChunks.length;
            tempChunks = new RPLEChunk[xChunksSize][];
            for (var xChunkIndex = 0; xChunkIndex < xChunksSize; xChunkIndex++) {
                val xBaseChunks = baseChunks[xChunkIndex];
                if (xBaseChunks == null)
                    continue;

                val zChunksSize = xBaseChunks.length;
                val xChunks = new RPLEChunk[zChunksSize];
                for (var zChunkIndex = 0; zChunkIndex < zChunksSize; zChunkIndex++) {
                    val zChunkBase = xBaseChunks[zChunkIndex];
                    if (!(zChunkBase instanceof RPLEChunk))
                        continue;
                    val zChunk = (RPLEChunk) zChunkBase;
                    xChunks[zChunkIndex] = zChunk;
                    tempIsEmpty = false;
                }

                tempChunks[xChunkIndex] = xChunks;
            }
        }

        if (tempIsEmpty) {
            chunks = null;
            isEmpty = true;
        } else {
            chunks = tempChunks;
            isEmpty = false;
        }
    }

    @Override
    public @NotNull ColorChannel rple$channel() {
        return channel;
    }

    @Override
    public @NotNull RPLEBlockCacheRoot lumi$root() {
        return root;
    }

    @Override
    public @NotNull RPLEWorld lumi$world() {
        return world;
    }

    @Override
    public int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
        var blockLightValue = rple$getChannelLightValueForRender(BLOCK_LIGHT_TYPE, posX, posY, posZ);
        blockLightValue = Math.max(blockLightValue, minBlockLight);
        val skyLightValue = rple$getChannelLightValueForRender(SKY_LIGHT_TYPE, posX, posY, posZ);
        return TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(blockLightValue, skyLightValue);
    }

    @Override
    public int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
        if (lightType == SKY_LIGHT_TYPE && !root.lumi$hasSky())
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

        val block = root.lumi$getBlock(posX, posY, posZ);
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

    @Override
    public @NotNull String lumi$BlockCacheID() {
        return blockCacheID;
    }

    @Override
    public @NotNull String lumi$blockStorageID() {
        return blockCacheID;
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
        val chunk = getChunkFromBlockPosIfExists(posX, posZ);
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
        val chunk = getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getLightValue(subChunkPosX, posY, subChunkPosZ);
        }
        return LightType.maxBaseLightValue();
    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int posX, int posY, int posZ) {
        val chunk = getChunkFromBlockPosIfExists(posX, posZ);
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
        val chunk = getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
        }
        return BLOCK_LIGHT_TYPE.defaultLightValue();
    }

    @Override
    public int lumi$getSkyLightValue(int posX, int posY, int posZ) {
        if (!root.lumi$hasSky())
            return 0;

        val chunk = getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
        }

        return SKY_LIGHT_TYPE.defaultLightValue();
    }

    @Override
    public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
        val block = root.lumi$getBlock(posX, posY, posZ);
        return block.getLightValue(blockCacheBase, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
        val block = root.lumi$getBlock(posX, posY, posZ);
        return block.getLightOpacity(blockCacheBase, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockBrightness(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
        return block.getLightValue(blockCacheBase, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(@NotNull Block block, int blockMeta, int posX, int posY, int posZ) {
        return block.getLightOpacity(blockCacheBase, posX, posY, posZ);
    }

    private int getNeighborLightValue(LightType lightType, int posX, int posY, int posZ, ForgeDirection direction) {
        posX += direction.offsetX;
        posY += direction.offsetY;
        posZ += direction.offsetZ;
        return lumi$getBrightness(lightType, posX, posY, posZ);
    }

    private @Nullable RPLEChunk getChunkFromBlockPosIfExists(int posX, int posZ) {
        checks:
        {
            if (isEmpty)
                break checks;

            val chunkPosX = posX >> 4;
            val chunkPosZ = posZ >> 4;
            val xChunkIndex = chunkPosX - chunkPosXOffset;
            val zChunkIndex = chunkPosZ - chunkPosZOffset;

            if (xChunkIndex < 0 || xChunkIndex >= chunks.length)
                break checks;

            val zChunkArray = chunks[xChunkIndex];
            if (zChunkArray == null)
                break checks;
            if (zChunkIndex < 0 || zChunkIndex >= zChunkArray.length)
                break checks;

            return zChunkArray[zChunkIndex];
        }
        return null;
    }
}
