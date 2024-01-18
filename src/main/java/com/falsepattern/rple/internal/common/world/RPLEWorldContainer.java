/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.world;

import com.falsepattern.lumina.api.LumiAPI;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.lumina.api.lighting.LumiLightingEngine;
import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunk;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkRoot;
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

public final class RPLEWorldContainer implements RPLEWorld {
    private static final String RED_WORLD_ID = MOD_ID + "_" + RED_CHANNEL + "_world";
    private static final String GREEN_WORLD_ID = MOD_ID + "_" + GREEN_CHANNEL + "_world";
    private static final String BLUE_WORLD_ID = MOD_ID + "_" + BLUE_CHANNEL + "_world";

    private final ColorChannel channel;
    private final World base;
    private final RPLEWorldRoot root;

    private final LumiLightingEngine lightingEngine;

    public RPLEWorldContainer(ColorChannel channel, World base, RPLEWorldRoot root, Profiler profiler) {
        this.channel = channel;
        this.base = base;
        this.root = root;

        this.lightingEngine = LumiAPI.provideLightingEngine(this, profiler);
    }

    // region World
    @Override
    public @NotNull ColorChannel rple$channel() {
        return channel;
    }

    @Override
    public @NotNull RPLEWorldRoot lumi$root() {
        return root;
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
        val lumiChunkRoot = root.lumi$getChunkRootFromBlockPosIfExists(posX, posZ);
        if (!(lumiChunkRoot instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) lumiChunkRoot;
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    @SuppressWarnings("InstanceofIncompatibleInterface")
    public @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        val lumiChunkRoot = root.lumi$getChunkRootFromChunkPosIfExists(chunkPosX, chunkPosZ);
        if (!(lumiChunkRoot instanceof RPLEChunkRoot))
            return null;
        val chunkRoot = (RPLEChunkRoot) lumiChunkRoot;
        return rpleChunkFromRootAndChannel(chunkRoot, channel);
    }

    private static RPLEChunk rpleChunkFromRootAndChannel(RPLEChunkRoot chunkRoot, ColorChannel channel) {
        return chunkRoot.rple$chunk(channel);
    }

    @Override
    public @NotNull LumiLightingEngine lumi$lightingEngine() {
        return lightingEngine;
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
    public void lumi$setBlockLightValue(int posX, int posY, int posZ, int lightValue) {
        val chunk = lumi$getChunkFromBlockPosIfExists(posX, posZ);
        if (chunk != null) {
            val subChunkPosX = posX & 15;
            val subChunkPosZ = posZ & 15;
            chunk.lumi$setBlockLightValue(subChunkPosX, posY, subChunkPosZ, lightValue);
        }
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
        val blockBase = root.lumi$getBlock(posX, posY, posZ);
        val blockMeta = root.lumi$getBlockMeta(posX, posY, posZ);
        return lumi$getBlockBrightness(blockBase, blockMeta, posX, posY, posZ);
    }

    @Override
    public int lumi$getBlockOpacity(int posX, int posY, int posZ) {
        val blockBase = root.lumi$getBlock(posX, posY, posZ);
        val blockMeta = root.lumi$getBlockMeta(posX, posY, posZ);
        return lumi$getBlockOpacity(blockBase, blockMeta, posX, posY, posZ);
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public int lumi$getBlockBrightness(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (RPLEBlock) blockBase;
//        val brightness = block.rple$getBrightnessColor(base, blockMeta, posX, posY, posZ);
//        return channel.componentFromColor(brightness);

//        return blockBase.getLightValue();

        return channel.componentFromColor(block.rple$getRawBrightnessColor(base, blockMeta, posX, posY, posZ));
    }

    @Override
    @SuppressWarnings("CastToIncompatibleInterface")
    public int lumi$getBlockOpacity(@NotNull Block blockBase, int blockMeta, int posX, int posY, int posZ) {
        val block = (RPLEBlock) blockBase;
//        val translucency = block.rple$getTranslucencyColor(base, blockMeta, posX, posY, posZ);
//        return invertColorComponent(channel.componentFromColor(translucency));

        return channel.componentFromColor(block.rple$getRawOpacityColor(base, blockMeta, posX, posY, posZ));

//        return blockBase.getLightOpacity();
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
    // endregion

    private int getNeighborLightValue(LightType lightType, int posX, int posY, int posZ, ForgeDirection direction) {
        posX += direction.offsetX;
        posY += direction.offsetY;
        posZ += direction.offsetZ;
        return lumi$getBrightness(lightType, posX, posY, posZ);
    }
}
