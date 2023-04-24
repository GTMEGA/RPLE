/*
 * Copyright (c) 2023 FalsePattern
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.storage;

import com.falsepattern.chunk.api.ChunkDataManager;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.Utils;
import lombok.val;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ColoredDataManager implements ChunkDataManager.SectionNBTDataManager, ChunkDataManager.PacketDataManager, ChunkDataManager.ChunkNBTDataManager {
    private final int colorChannel;
    private final boolean root;

    public ColoredDataManager(int colorChannel, boolean root) {
        this.colorChannel = colorChannel;
        this.root = root;
    }

    @Override
    public int maxPacketSize() {
        return 16 * (
                16 * 16 * 8 * 2
        ) + 256;
    }

    @Override
    public void writeToBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer data) {
        val carrier = (ColoredCarrierChunk) chunk;
        boolean hasSky = !chunk.worldObj.provider.hasNoSky;
        val cn = carrier.getColoredChunk(colorChannel);
        for (int i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) != 0) {
                val ebs = cn.lumiEBS(i);
                if (ebs != null) {
                    data.put(ebs.lumiBlocklightArray().data);
                    if (hasSky) {
                        data.put(ebs.lumiSkylightArray().data);
                    }
                }
            }
        }
    }

    @Override
    public void readFromBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer buffer) {
        val carrier = (ColoredCarrierChunk) chunk;
        boolean hasSky = !chunk.worldObj.provider.hasNoSky;
        val cn = carrier.getColoredChunk(colorChannel);
        for (int i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) != 0) {
                val ebs = cn.lumiEBS(i);
                if (ebs != null) {
                    buffer.get(ebs.lumiBlocklightArray().data);
                    if (hasSky) {
                        buffer.get(ebs.lumiSkylightArray().data);
                    }
                }
            }
        }
    }

    @Override
    public void writeSectionToNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val cEbs = ((ColoredCarrierEBS)ebs).getColoredEBS(colorChannel);
        section.setByteArray("BlockLight", cEbs.lumiBlocklightArray().data);
        if (!chunk.worldObj.provider.hasNoSky) {
            section.setByteArray("SkyLight", cEbs.lumiSkylightArray().data);
        }
    }

    @Override
    public void readSectionFromNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val cEbs = ((ColoredCarrierEBS)ebs).getColoredEBS(colorChannel);
        safeNibbleArray(section, "BlockLight", cEbs.lumiBlocklightArray(), EnumSkyBlock.Block);
        if (!chunk.worldObj.provider.hasNoSky) {
            safeNibbleArray(section, "SkyLight", cEbs.lumiSkylightArray(), EnumSkyBlock.Sky);
        }
    }

    private static void safeNibbleArray(NBTTagCompound compound, String key, NibbleArray array, EnumSkyBlock esb) {
        if (compound.hasKey(key, 7)) {
            val arr = compound.getByteArray(key);
            if (arr.length == 2048) {
                System.arraycopy(arr, 0, array.data, 0, 2048);
                return;
            }
        }
        Arrays.fill(array.data, (byte)esb.defaultLightValue);
    }

    @Override
    public String domain() {
        return Tags.MODID;
    }

    @Override
    public String id() {
        return Utils.IDs[colorChannel];
    }

    @Override
    public void writeChunkToNBT(Chunk chunk, NBTTagCompound nbt) {
        if (!root)
            nbt.setIntArray("HeightMap", ((ColoredCarrierChunk)chunk).getColoredChunk(colorChannel).lumiHeightMap());
    }

    @Override
    public void readChunkFromNBT(Chunk chunk, NBTTagCompound nbt) {
        if (!root) {
            val heightMap = nbt.getIntArray("HeightMap");
            val lhm = ((ColoredCarrierChunk) chunk).getColoredChunk(colorChannel).lumiHeightMap();
            if (heightMap != null && heightMap.length == 256) {
                System.arraycopy(heightMap, 0, lhm, 0, 256);
            } else {
                System.arraycopy(chunk.heightMap, 0, lhm, 0, 256);
            }
        }
    }

    @Override
    public boolean chunkPrivilegedAccess() {
        return root;
    }
}
