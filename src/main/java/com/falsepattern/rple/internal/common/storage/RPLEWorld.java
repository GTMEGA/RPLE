/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.engine.LumiLightingEngine;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.lumina.api.world.LumiWorldRoot;
import com.falsepattern.rple.api.RPLEColorAPI;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import static com.falsepattern.rple.api.RPLEBlockAPI.getColoredBrightnessSafe;
import static com.falsepattern.rple.api.RPLEBlockAPI.getColoredTranslucencySafe;


@Accessors(fluent = true, chain = false)
public final class RPLEWorld implements LumiWorld {
    private final LumiWorld delegate;
    @Getter
    private final ColorChannel channel;

    @Getter
    private final String luminaWorldID;

    @Setter
    @Getter
    private LumiLightingEngine lightingEngine;

    public RPLEWorld(LumiWorld delegate, ColorChannel channel) {
        this.delegate = delegate;
        this.channel = channel;

        this.luminaWorldID = Tags.MODID + "_" + Tags.VERSION + "_" + channel.name();
    }

    @Override
    public RPLEChunk toLumiChunk(Chunk chunk) {
        val rpleChunkRoot = (RPLEChunkRoot) chunk;
        return rpleChunkRoot.rpleChunk(channel);
    }

    @Override
    public RPLESubChunk toLumiSubChunk(ExtendedBlockStorage subChunk) {
        val rpleSubChunkRoot = (RPLESubChunkRoot) subChunk;
        return rpleSubChunkRoot.rpleSubChunk(channel);
    }

    @Override
    public int lumiGetLightValue(Block block, int blockMeta, int posX, int posY, int posZ) {
        return getLightValue((World) delegate, block, blockMeta, posX, posY, posZ);
    }

    public int getLightValue(IBlockAccess world, Block block, int blockMeta, int posX, int posY, int posZ) {
        val color = getColoredBrightnessSafe(world, block, blockMeta, posX, posY, posZ);
        return channel.componentFromColor(color);
    }

    @Override
    public int lumiGetLightOpacity(Block block, int blockMeta, int posX, int posY, int posZ) {
        return getLightOpacity((World) delegate, block, blockMeta, posX, posY, posZ);
    }

    public int getLightOpacity(IBlockAccess world, Block block, int blockMeta, int posX, int posY, int posZ) {
        val color = getColoredTranslucencySafe(world, block, blockMeta, posX, posY, posZ);
        return RPLEColorAPI.invertColorComponent(channel.componentFromColor(color));
    }

    @Override
    public LumiWorldRoot worldRoot() {
        return delegate.worldRoot();
    }

    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocksWorld(IBlockAccess access, int x, int y, int z, int minBlock) {
        val sky = getSkyBlockTypeBrightness(access, EnumSkyBlock.Sky, x, y, z);
        var block = getSkyBlockTypeBrightness(access, EnumSkyBlock.Block, x, y, z);

        if (block < minBlock)
            block = minBlock;
        return BrightnessUtil.lightLevelsToBrightness(block, sky);
    }

    @SideOnly(Side.CLIENT)
    private int getSkyBlockTypeBrightness(IBlockAccess access, EnumSkyBlock skyBlock, int x, int y, int z) {
        if (y < 0)
            y = 0;

        if (y >= 256)
            return skyBlock.defaultLightValue;

        if (x < -30000000 || z < -30000000 || x >= 30000000 || z > 30000000)
            return skyBlock.defaultLightValue;

        if (skyBlock == EnumSkyBlock.Sky && !delegate.worldRoot().hasSkyLight())
            return 0;

        int cX;
        int cZ;
        if (access.getBlock(x, y, z).getUseNeighborBrightness()) {
            cX = getSavedLightValue(access, skyBlock, x, y + 1, z);
            cZ = getSavedLightValue(access, skyBlock, x + 1, y, z);

            val j1 = getSavedLightValue(access, skyBlock, x - 1, y, z);
            val k1 = getSavedLightValue(access, skyBlock, x, y, z + 1);
            val l1 = getSavedLightValue(access, skyBlock, x, y, z - 1);

            if (cZ > cX)
                cX = cZ;

            if (j1 > cX)
                cX = j1;

            if (k1 > cX)
                cX = k1;

            if (l1 > cX)
                cX = l1;

            return cX;
        } else {
            cX = x >> 4;
            cZ = z >> 4;

            final Chunk vanillaChunk;
            if (access instanceof ChunkCache) {
                val cc = (ChunkCache) access;
                cX -= cc.chunkX;
                cZ -= cc.chunkZ;
                vanillaChunk = cc.chunkArray[cX][cZ];
            } else if (access instanceof World) {
                val world = (World) access;
                vanillaChunk = world.getChunkFromChunkCoords(cX, cZ);
            } else {
                return skyBlock.defaultLightValue;
            }

            val chunk = toLumiChunk(vanillaChunk);
            if (skyBlock == EnumSkyBlock.Block)
                return getIntrinsicOrSavedBlockLightValue(access, chunk, x & 15, y, z & 15);
            return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
        }
    }


    @SideOnly(Side.CLIENT)
    public int getSavedLightValue(IBlockAccess access, EnumSkyBlock skyBlock, int x, int y, int z) {
        if (y < 0)
            y = 0;

        if (y >= 256)
            y = 255;

        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000)
            return skyBlock.defaultLightValue;

        int cX = x >> 4;
        int cZ = z >> 4;

        if (access instanceof World && !((World) access).chunkExists(cX, cZ))
            return skyBlock.defaultLightValue;

        final Chunk vanillaChunk;
        if (access instanceof ChunkCache) {
            val cc = (ChunkCache) access;
            cX -= cc.chunkX;
            cZ -= cc.chunkZ;
            vanillaChunk = cc.chunkArray[cX][cZ];
        } else if (access instanceof World) {
            vanillaChunk = ((World) access).getChunkFromChunkCoords(cX, cZ);
        } else {
            return skyBlock.defaultLightValue;
        }

        val chunk = toLumiChunk(vanillaChunk);
        if (skyBlock == EnumSkyBlock.Block)
            return getIntrinsicOrSavedBlockLightValue(access, chunk, x & 15, y, z & 15);
        return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
    }

    @Deprecated
    private int getIntrinsicOrSavedBlockLightValue(IBlockAccess access, RPLEChunk chunk, int subChunkPosX, int posY, int subChunkPosZ) {
        val chunkRoot = chunk.chunkRoot();

        val posX = subChunkPosX + (chunk.chunkPosX() * 16);
        val posZ = subChunkPosZ + (chunk.chunkPosZ() * 16);

        val block = chunkRoot.getBlock(subChunkPosX, posY, subChunkPosZ);
        val blockMeta = chunkRoot.getBlockMeta(subChunkPosX, posY, subChunkPosZ);

        val savedLightValue = chunk.getSavedLightValue(EnumSkyBlock.Block, subChunkPosX, posY, subChunkPosZ);
        val lightValue = getLightValue(access, block, blockMeta, posX, posY, posZ);
        return Math.max(savedLightValue, lightValue);
    }
}
