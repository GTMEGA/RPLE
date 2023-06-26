/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.lumina.api.ILightingEngine;
import com.falsepattern.lumina.api.ILumiWorld;
import com.falsepattern.lumina.api.ILumiWorldRoot;
import com.falsepattern.rple.api.OldColoredBlock;
import com.falsepattern.rple.internal.RPLE;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.helper.BrightnessUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public final class ColoredLightWorld implements ILumiWorld {
    @Getter
    @Setter
    private ILightingEngine lightingEngine;
    private final World carrier;
    private final String id;

    public final int colorChannel;

    public ColoredLightWorld(World world, int colorChannel) {
        this.carrier = world;
        this.colorChannel = colorChannel;
        this.id = Tags.MODID + "_" + Tags.VERSION + "_" + RPLE.IDs[colorChannel];
    }

    @Override
    public ColoredLightChunk lumiWrap(Chunk chunk) {
        val carrierChunk = (ColoredCarrierChunk) chunk;
        return carrierChunk.getColoredChunk(colorChannel);
    }

    @Override
    public ColoredLightEBS lumiWrap(ExtendedBlockStorage ebs) {
        val carrierEBS = (ColoredCarrierEBS) ebs;
        return carrierEBS.getColoredEBS(colorChannel);
    }

    @Override
    public int lumiGetLightValue(Block block, int meta, int x, int y, int z) {
        return getLightValue(carrier, block, meta, x, y, z);
    }

    // TODO: [PRE_RELEASE] Replace the cast
    public int getLightValue(IBlockAccess access, Block block, int meta, int x, int y, int z) {
        val colouredBlock = ((OldColoredBlock) block);
        return colouredBlock.getColoredLightValue(access, meta, colorChannel, x, y, z);
    }

    @Override
    public int lumiGetLightOpacity(Block block, int meta, int x, int y, int z) {
        return getLightOpacity(carrier, block, meta, x, y, z);
    }

    // TODO: [PRE_RELEASE] Replace the cast
    public int getLightOpacity(IBlockAccess access, Block block, int meta, int x, int y, int z) {
        val colouredBlock = ((OldColoredBlock) block);
        return colouredBlock.getColoredLightOpacity(access, meta, colorChannel, x, y, z);
    }

    @Override
    public String lumiId() {
        return id;
    }

    @Override
    public ILumiWorldRoot root() {
        return (ILumiWorldRoot) carrier;
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

        if (skyBlock == EnumSkyBlock.Sky && carrier.provider.hasNoSky)
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

            val chunk = lumiWrap(vanillaChunk);
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

        val chunk = lumiWrap(vanillaChunk);
        if (skyBlock == EnumSkyBlock.Block)
            return getIntrinsicOrSavedBlockLightValue(access, chunk, x & 15, y, z & 15);
        return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
    }

    private int getIntrinsicOrSavedBlockLightValue(IBlockAccess access, ColoredLightChunk chunk, int x, int y, int z) {
        val savedLightValue = chunk.getSavedLightValue(EnumSkyBlock.Block, x, y, z);

        val block = chunk.root().rootGetBlock(x, y, z);
        val meta = chunk.root().rootGetBlockMetadata(x, y, z);
        val bx = x + (chunk.x() * 16);
        val bz = z + (chunk.z() * 16);

        val lightValue = getLightValue(access, block, meta, bx, y, bz);
        return Math.max(savedLightValue, lightValue);
    }
}
