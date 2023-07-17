/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage.world;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.block.ColoredLightBlock;
import com.falsepattern.rple.api.block.ColoredTranslucentBlock;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.storage.chunk.RPLESubChunk;
import com.falsepattern.rple.internal.common.storage.chunk.RPLESubChunkRoot;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.lumina.api.lighting.LightType.BLOCK_LIGHT_TYPE;
import static com.falsepattern.lumina.api.lighting.LightType.SKY_LIGHT_TYPE;
import static com.falsepattern.rple.api.RPLEColorAPI.invertColorComponent;


public final class RPLEWorldWrapper implements RPLEWorld {
    private final ColorChannel channel;
    private final String worldID;
    private final World worldBase;
    private final RPLEWorldRoot worldRoot;
    private final LumiLightingEngine lightingEngine;

    public RPLEWorldWrapper(ColorChannel channel, World worldBase, RPLEWorldRoot worldRoot, LumiWorld lumiWorld) {
        this.channel = channel;
        this.worldID = Tags.MOD_ID + "_" + channel;
        this.worldBase = worldBase;
        this.worldRoot = worldRoot;
        this.lightingEngine = lumiWorld.lumi$lightingEngine();
    }

    @Override
    public ColorChannel rple$channel() {
        return channel;
    }

    @Override
    public @NotNull RPLEWorldRoot lumi$root() {
        return worldRoot;
    }

    @Override
    public @NotNull String lumi$worldID() {
        return worldID;
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
        val chunkBase = worldBase.getChunkFromBlockCoords(posX, posZ);
        if (!(chunkBase instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) chunkBase;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        val chunkBase = worldBase.getChunkFromChunkCoords(chunkPosX, chunkPosZ);
        if (!(chunkBase instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) chunkBase;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    public @NotNull LumiLightingEngine lumi$lightingEngine() {
        return lightingEngine;
    }

    @Override
    public int lumi$getBrightnessAndLightValueMax(@NotNull LightType lightType, int posX, int posY, int posZ) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                return lumi$getBrightnessAndBlockLightValueMax(posX, posY, posZ);
            case SKY_LIGHT_TYPE:
                return lumi$getSkyLightValue(posX, posY, posZ);
            default:
                return 0;
        }
    }

    @Override
    public int lumi$getBrightnessAndBlockLightValueMax(int posX, int posY, int posZ) {
        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getBrightnessAndBlockLightValueMax(subChunkPosX, posY, subChunkPosZ);
        }
        val blockBrightness = lumi$getBlockBrightness(posX, posY, posZ);
        return Math.max(blockBrightness, BLOCK_LIGHT_TYPE.defaultLightValue());
    }

    @Override
    public int lumi$getLightValueMax(int posX, int posY, int posZ) {
        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            return chunk.lumi$getLightValueMax(subChunkPosX, posY, subChunkPosZ);
        }
        return LightType.maxBaseLightValue();
    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType, int posX, int posY, int posZ, int lightValue) {
        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setLightValue(lightType, subChunkPosX, posY, subChunkPosZ, lightValue);
        }
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
    public void lumi$setBlockLightValue(int posX, int posY, int posZ, int lightValue) {
        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setBlockLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
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
    public void lumi$setSkyLightValue(int posX, int posY, int posZ, int lightValue) {
        if (!lumi$root().lumi$hasSky())
            return;

        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setSkyLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
        }
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
        val block = worldRoot.lumi$getBlock(posX, posY, posZ);
        return block.getLightValue(worldBase, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
        val block = worldRoot.lumi$getBlock(posX, posY, posZ);
        return block.getLightOpacity(worldBase, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockBrightness(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (ColoredLightBlock) blockBase;
        val brightness = block.getColoredBrightness(worldBase, blockMeta, posX, posY, posZ);
        return channel.componentFromColor(brightness);
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public int lumi$getBlockOpacity(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (ColoredTranslucentBlock) blockBase;
        val translucency = block.getColoredTranslucency(worldBase, blockMeta, posX, posY, posZ);
        return invertColorComponent(channel.componentFromColor(translucency));
    }
}