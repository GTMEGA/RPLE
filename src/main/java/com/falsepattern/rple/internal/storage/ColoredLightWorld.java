/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.storage;

import com.falsepattern.lumina.api.ILightingEngine;
import com.falsepattern.lumina.api.ILumiWorld;
import com.falsepattern.lumina.api.ILumiWorldRoot;
import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ColoredLightWorld implements ILumiWorld {
    @Getter
    @Setter
    private ILightingEngine lightingEngine;
    private final World carrier;
    public final int colorChannel;
    private final String id;

    public ColoredLightWorld(World world, int colorChannel) {
        this.carrier = world;
        this.colorChannel = colorChannel;
        id = Tags.MODID + "_" + Utils.IDs[colorChannel];
    }

    @Override
    public ColoredLightChunk lumiWrap(Chunk chunk) {
        return ((ColoredCarrierChunk)chunk).getColoredChunk(colorChannel);
    }

    @Override
    public ColoredLightEBS lumiWrap(ExtendedBlockStorage ebs) {
        return ((ColoredCarrierEBS)ebs).getColoredEBS(colorChannel);
    }

    @Override
    public int lumiGetLightValue(Block block, int meta, int x, int y, int z) {
        return getLightValue(carrier, block, meta, x, y, z);
    }

    public int getLightValue(IBlockAccess access, Block block, int meta, int x, int y, int z) {
        return ((ColoredBlock)block).getColoredLightValue(access, meta, colorChannel, x, y, z);
    }

    @Override
    public int lumiGetLightOpacity(Block block, int meta, int x, int y, int z) {
        return getLightOpacity(carrier, block, meta, x, y, z);
    }

    public int getLightOpacity(IBlockAccess access, Block block, int meta, int x, int y, int z) {
        return ((ColoredBlock)block).getColoredLightOpacity(access, meta, colorChannel, x, y, z);
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
        int sky = this.getSkyBlockTypeBrightness(access, EnumSkyBlock.Sky, x, y, z);
        int block = this.getSkyBlockTypeBrightness(access, EnumSkyBlock.Block, x, y, z);

        if (block < minBlock) {
            block = minBlock;
        }
        return Utils.createPair(block, sky);
    }

    @SideOnly(Side.CLIENT)
    private int getSkyBlockTypeBrightness(IBlockAccess access, EnumSkyBlock skyBlock, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= 256) {
            return skyBlock.defaultLightValue;
        }

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z <= 30000000) {
            if (skyBlock == EnumSkyBlock.Sky && carrier.provider.hasNoSky) {
                return 0;
            } else {
                int cX;
                int cZ;

                if (access.getBlock(x, y, z).getUseNeighborBrightness()) {
                    cX = getSavedLightValue(access, skyBlock, x, y + 1, z);
                    cZ = getSavedLightValue(access, skyBlock, x + 1, y, z);
                    int j1 = getSavedLightValue(access, skyBlock, x - 1, y, z);
                    int k1 = getSavedLightValue(access, skyBlock, x, y, z + 1);
                    int l1 = getSavedLightValue(access, skyBlock, x, y, z - 1);

                    if (cZ > cX) {
                        cX = cZ;
                    }

                    if (j1 > cX) {
                        cX = j1;
                    }

                    if (k1 > cX) {
                        cX = k1;
                    }

                    if (l1 > cX) {
                        cX = l1;
                    }

                    return cX;
                } else {
                    cX = (x >> 4);
                    cZ = (z >> 4);
                    Chunk vanillaChunk;
                    if (access instanceof ChunkCache) {
                        val cc = (ChunkCache) access;
                        cX -= cc.chunkX;
                        cZ -= cc.chunkZ;
                        vanillaChunk = cc.chunkArray[cX][cZ];
                    } else if (access instanceof World) {
                        vanillaChunk = ((World)access).getChunkFromChunkCoords(cX, cZ);
                    } else {
                        return skyBlock.defaultLightValue;
                    }
                    val chunk = lumiWrap(vanillaChunk);
                    if (skyBlock == EnumSkyBlock.Block)
                        return getIntrinsicOrSavedBlockLightValue(access, chunk, x & 15, y, z & 15);
                    else
                        return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
                }
            }
        } else {
            return skyBlock.defaultLightValue;
        }
    }



    @SideOnly(Side.CLIENT)
    public int getSavedLightValue(IBlockAccess access, EnumSkyBlock skyBlock, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= 256) {
            y = 255;
        }

        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            int cX = x >> 4;
            int cZ = z >> 4;

            if (access instanceof World && !((World)access).chunkExists(cX, cZ)) {
                return skyBlock.defaultLightValue;
            } else {
                Chunk vanillaChunk;
                if (access instanceof ChunkCache) {
                    val cc = (ChunkCache) access;
                    cX -= cc.chunkX;
                    cZ -= cc.chunkZ;
                    vanillaChunk = cc.chunkArray[cX][cZ];
                } else if (access instanceof World) {
                    vanillaChunk = ((World)access).getChunkFromChunkCoords(cX, cZ);
                } else {
                    return skyBlock.defaultLightValue;
                }
                ColoredLightChunk chunk = lumiWrap(vanillaChunk);

                if(skyBlock == EnumSkyBlock.Block)
                    return getIntrinsicOrSavedBlockLightValue(access, chunk, x & 15, y, z & 15);
                else
                    return chunk.getSavedLightValue(skyBlock, x & 15, y, z & 15);
            }
        } else {
            return skyBlock.defaultLightValue;
        }
    }

    private int getIntrinsicOrSavedBlockLightValue(IBlockAccess access, ColoredLightChunk chunk, int x, int y, int z) {
        int savedLightValue = chunk.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
        int bx = x + (chunk.x() * 16);
        int bz = z + (chunk.z() * 16);
        Block block = chunk.root().rootGetBlock(x, y, z);
        int meta = chunk.root().rootGetBlockMetadata(x, y, z);
        int lightValue = getLightValue(access, block, meta, bx, y, bz);
        return Math.max(savedLightValue, lightValue);
    }
}
