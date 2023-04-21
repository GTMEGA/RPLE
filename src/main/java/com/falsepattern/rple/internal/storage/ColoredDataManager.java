package com.falsepattern.rple.internal.storage;

import com.falsepattern.chunk.api.ChunkDataManager;
import com.falsepattern.rple.internal.Tags;
import lombok.val;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ColoredDataManager implements ChunkDataManager.SectionNBTDataManager, ChunkDataManager.PacketDataManager, ChunkDataManager.ChunkNBTDataManager {
    private final ColoredLightChannel channel;

    public ColoredDataManager(ColoredLightChannel channel) {
        this.channel = channel;
    }

    @Override
    public int maxPacketSize() {
        return 16 * (
                16 * 16 * 8
        );
    }

    @Override
    public void writeToBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer data) {
        val carrier = (ColoredCarrierChunk) chunk;
        boolean hasSky = !chunk.worldObj.provider.hasNoSky;
        val cn = carrier.getColoredChunk(channel);
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
        val cn = carrier.getColoredChunk(channel);
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
        val cEbs = ((ColoredCarrierEBS)ebs).getColoredEBS(channel);
        section.setByteArray("BlockLight", cEbs.lumiBlocklightArray().data);
        if (!chunk.worldObj.provider.hasNoSky) {
            section.setByteArray("SkyLight", cEbs.lumiSkylightArray().data);
        } else {
            section.setByteArray("SkyLight", new byte[0]);
        }
    }

    @Override
    public void readSectionFromNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val cEbs = ((ColoredCarrierEBS)ebs).getColoredEBS(channel);
        System.arraycopy(section.getByteArray("BlockLight"), 0, cEbs.lumiBlocklightArray().data, 0, 2048);
        if (!chunk.worldObj.provider.hasNoSky) {
            val arr = section.getByteArray("SkyLight");
            if (arr.length == 2048) {
                System.arraycopy(arr, 0, cEbs.lumiSkylightArray().data, 0, 2048);
            } else {
                Arrays.fill(cEbs.lumiSkylightArray().data, (byte)0);
            }
        }
    }

    @Override
    public String domain() {
        return Tags.MODID;
    }

    @Override
    public String id() {
        return channel.name();
    }

    @Override
    public void writeChunkToNBT(Chunk chunk, NBTTagCompound nbt) {
        nbt.setIntArray("HeightMap", ((ColoredCarrierChunk)chunk).getColoredChunk(channel).lumiHeightMap());
    }

    @Override
    public void readChunkFromNBT(Chunk chunk, NBTTagCompound nbt) {
        val heightMap = nbt.getIntArray("HeightMap");
        val lhm = ((ColoredCarrierChunk)chunk).getColoredChunk(channel).lumiHeightMap();
        if (heightMap != null && heightMap.length == 256) {
            System.arraycopy(heightMap, 0, lhm, 0, 256);
        } else {
            System.arraycopy(chunk.heightMap, 0, lhm, 0, 256);
        }
    }
}
