/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumina.api.LumiAPI;
import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunk;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkRoot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.lumina.api.lighting.LightType.SKY_LIGHT_TYPE;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.Tags.MOD_ID;
import static net.minecraftforge.common.util.ForgeDirection.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RPLEWorldContainer implements RPLEWorld {
    private static final String RED_WORLD_ID = MOD_ID + "_" + RED_CHANNEL + "_world";
    private static final String GREEN_WORLD_ID = MOD_ID + "_" + GREEN_CHANNEL + "_world";
    private static final String BLUE_WORLD_ID = MOD_ID + "_" + BLUE_CHANNEL + "_world";

    private final ColorChannel channel;
    private final World base;
    private final RPLEWorldRoot worldRoot;
    private final RPLEBlockStorageRoot blockStorageRoot;

    private final LumiLightingEngine lightingEngine;

    public RPLEWorldContainer(ColorChannel channel, World base, RPLEWorldRoot root, Profiler profiler) {
        this.channel = channel;
        this.base = base;
        this.worldRoot = root;
        this.blockStorageRoot = root;

        this.lightingEngine = LumiAPI.provideLightingEngine(this, profiler);
    }

    // region World
    @Override
    public @NotNull ColorChannel rple$channel() {
        return channel;
    }

    @Override
    public @NotNull RPLEWorldRoot lumi$root() {
        return worldRoot;
    }

    @Override
    public RPLEWorld getCloneForChunkCache(RPLEBlockStorageRoot chunkCache) {
        return new RPLEWorldContainer(channel, base, worldRoot, chunkCache, lightingEngine);
    }

    @Override
    public @NotNull String lumi$worldID() {
        switch (channel) {
            default:
            case RED_CHANNEL:
                return RED_WORLD_ID;
            case GREEN_CHANNEL:
                return GREEN_WORLD_ID;
            case BLUE_CHANNEL:
                return BLUE_WORLD_ID;
        }
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public @NotNull RPLEChunk lumi$wrap(@NotNull Chunk chunkBase) {
        val chunkRoot = (RPLEChunkRoot) chunkBase;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public @NotNull RPLESubChunk lumi$wrap(@NotNull ExtendedBlockStorage subChunkBase) {
        val chunkSubRoot = (RPLESubChunkRoot) subChunkBase;
        return chunkSubRoot.rple$subChunk(channel);
    }

    @Override
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable RPLEChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ) {
        val lumiChunkRoot = blockStorageRoot.lumi$getChunkRootFromBlockPosIfExists(posX, posZ);
        if (!(lumiChunkRoot instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) lumiChunkRoot;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        val lumiChunkRoot = blockStorageRoot.lumi$getChunkRootFromChunkPosIfExists(chunkPosX, chunkPosZ);
        if (!(lumiChunkRoot instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) lumiChunkRoot;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    public @NotNull LumiLightingEngine lumi$lightingEngine() {
        return lightingEngine;
    }

    @Override
    public void lumi$setLightValue(@Nullable LumiChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ, int lightValue) {
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setLightValue(lightType, subChunkPosX, posY, subChunkPosZ, lightValue);
        }
    }

    @Override
    public void lumi$setBlockLightValue(@Nullable LumiChunk chunk, int posX, int posY, int posZ, int lightValue) {
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setBlockLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
        }
    }

    @Override
    public void lumi$setSkyLightValue(@Nullable LumiChunk chunk, int posX, int posY, int posZ, int lightValue) {
        if (!lumi$root().lumi$hasSky())
            return;

        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setSkyLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
        }
    }
    // endregion

    // region Block Storage
    @Override
    public @NotNull String lumi$blockStorageID() {
        return lumi$worldID();
    }

    @Override
    public @NotNull RPLEWorld lumi$world() {
        return this;
    }

    @Override
    public int lumi$getBrightness(@Nullable LumiChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                return lumi$getBrightness(chunk, posX, posY, posZ);
            case SKY_LIGHT_TYPE:
                return lumi$getSkyLightValue(chunk, posX, posY, posZ);
            default:
                return 0;
        }
    }

    @Override
    public int lumi$getBrightness(@Nullable LumiChunk chunk, int posX, int posY, int posZ) {
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getBrightness(subChunkPosX, posY, subChunkPosZ);
        }
        val blockBrightness = lumi$getBlockBrightness(posX, posY, posZ);
        return Math.max(blockBrightness, BLOCK_LIGHT_TYPE.defaultLightValue());
    }

    @Override
    public int lumi$getLightValue(@Nullable LumiChunk chunk, int posX, int posY, int posZ) {
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getLightValue(subChunkPosX, posY, subChunkPosZ);
        }
        return LightType.maxBaseLightValue();
    }

    @Override
    public int lumi$getLightValue(@Nullable LumiChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ) {
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
    public int lumi$getBlockLightValue(@Nullable LumiChunk chunk, int posX, int posY, int posZ) {
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getBlockLightValue(subChunkPosX, posY, subChunkPosZ);
        }
        return BLOCK_LIGHT_TYPE.defaultLightValue();
    }

    @Override
    public int lumi$getSkyLightValue(@Nullable LumiChunk chunk, int posX, int posY, int posZ) {
        if (!lumi$root().lumi$hasSky())
            return 0;

        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getSkyLightValue(subChunkPosX, posY, subChunkPosZ);
        }

        return SKY_LIGHT_TYPE.defaultLightValue();
    }

    @Override
    public int lumi$getBlockBrightness(int posX, int posY, int posZ) {
        val blockBase = blockStorageRoot.lumi$getBlock(posX, posY, posZ);
        val blockMeta = blockStorageRoot.lumi$getBlockMeta(posX, posY, posZ);
        return lumi$getBlockBrightness(blockBase, blockMeta, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
        val blockBase = blockStorageRoot.lumi$getBlock(posX, posY, posZ);
        val blockMeta = blockStorageRoot.lumi$getBlockMeta(posX, posY, posZ);
        return lumi$getBlockOpacity(blockBase, blockMeta, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public int lumi$getBlockBrightness(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (RPLEBlock) blockBase;
        return channel.componentFromColor(block.rple$getRawBrightnessColor(base, blockMeta, posX, posY, posZ));
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public int lumi$getBlockOpacity(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (RPLEBlock) blockBase;
        return channel.componentFromColor(block.rple$getRawOpacityColor(base, blockMeta, posX, posY, posZ));
    }

    @Override
    public int rple$getChannelBrightnessForTessellator(@Nullable RPLEChunk chunk, int posX, int posY, int posZ, int minBlockLight) {
        var blockLightValue = rple$getChannelLightValueForRender(chunk, BLOCK_LIGHT_TYPE, posX, posY, posZ);
        blockLightValue = Math.max(blockLightValue, minBlockLight);
        val skyLightValue = rple$getChannelLightValueForRender(chunk, SKY_LIGHT_TYPE, posX, posY, posZ);
        return TessellatorBrightnessHelper.lightLevelsToBrightnessForTessellator(blockLightValue, skyLightValue);
    }

    @Override
    public int rple$getChannelLightValueForRender(@Nullable RPLEChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ) {
        if (lightType == SKY_LIGHT_TYPE && !blockStorageRoot.lumi$hasSky())
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

        val block = blockStorageRoot.lumi$getBlock(posX, posY, posZ);
        if (block.getUseNeighborBrightness()) {
            var lightValue = 0;
            lightValue = Math.max(lightValue, getNeighborLightValue(chunk, lightType, posX, posY, posZ, UP));
            lightValue = Math.max(lightValue, getNeighborLightValue(chunk, lightType, posX, posY, posZ, NORTH));
            lightValue = Math.max(lightValue, getNeighborLightValue(chunk, lightType, posX, posY, posZ, SOUTH));
            lightValue = Math.max(lightValue, getNeighborLightValue(chunk, lightType, posX, posY, posZ, WEST));
            lightValue = Math.max(lightValue, getNeighborLightValue(chunk, lightType, posX, posY, posZ, EAST));
            return lightValue;
        }
        return lumi$getBrightness(chunk, lightType, posX, posY, posZ);
    }
    // endregion

    private int getNeighborLightValue(@Nullable RPLEChunk chunk, LightType lightType, int posX, int posY, int posZ, ForgeDirection direction) {
        int chunkXPre = posX >> 4;
        int chunkZPre = posZ >> 4;
        posX += direction.offsetX;
        posY += direction.offsetY;
        posZ += direction.offsetZ;
        int chunkXPost = posX >> 4;
        int chunkZPost = posZ >> 4;
        if (chunkXPre != chunkXPost || chunkZPre != chunkZPost) {
            chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        }
        return lumi$getBrightness(chunk, lightType, posX, posY, posZ);
    }
}
