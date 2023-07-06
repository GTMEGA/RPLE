/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.storage;

import com.falsepattern.chunk.api.ChunkDataManager;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.Tags;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.Constants;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public final class ColoredDataManager implements ChunkDataManager.SectionNBTDataManager, ChunkDataManager.PacketDataManager {
    private static final int VERSION_HASH = Tags.VERSION.hashCode();

    private final ColorChannel channel;

    @Override
    public int maxPacketSize() {
        return 16 * 16 * 16 * 8 * 2 + 256;
    }

    @Override
    public void writeToBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer data) {
        val rpleChunkRoot = (RPLEChunkRoot) chunk;
        val rpleChunk = rpleChunkRoot.rpleChunk(channel);
        val hasSky = !chunk.worldObj.provider.hasNoSky;
        for (var i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) == 0)
                continue;

            val rpleSubChunk = rpleChunk.subChunk(i);
            if (rpleSubChunk == null)
                continue;

            data.put(rpleSubChunk.blockLight().data);
            if (hasSky)
                data.put(rpleSubChunk.skyLight().data);
        }
    }

    @Override
    public void readFromBuffer(Chunk chunk, int ebsMask, boolean forceUpdate, ByteBuffer buffer) {
        val rpleChunkRoot = (RPLEChunkRoot) chunk;
        val rpleChunk = rpleChunkRoot.rpleChunk(channel);
        val hasSky = !chunk.worldObj.provider.hasNoSky;
        for (var i = 0; i < 16; i++) {
            if ((ebsMask & (1 << i)) == 0)
                continue;

            val rpleSubChunk = rpleChunk.subChunk(i);
            if (rpleSubChunk == null)
                continue;

            buffer.get(rpleSubChunk.blockLight().data);
            if (hasSky)
                buffer.get(rpleSubChunk.skyLight().data);
        }
    }

    @Override
    public void writeSectionToNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        val rpleSubChunkRoot = (RPLESubChunkRoot)ebs;
        val rpleSubChunk = rpleSubChunkRoot.rpleSubChunk(channel);
        val hasSky = !chunk.worldObj.provider.hasNoSky;

        section.setInteger("v", VERSION_HASH);
        section.setByteArray("BlockLight", rpleSubChunk.blockLight().data);

        if (hasSky)
            section.setByteArray("SkyLight", rpleSubChunk.skyLight().data);
    }

    @Override
    public void readSectionFromNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound section) {
        if (!section.hasKey("v", Constants.NBT.TAG_INT))
            return;

        val ver = section.getInteger("v");
        if (ver != VERSION_HASH)
            return;

        val rpleSubChunkRoot = (RPLESubChunkRoot)ebs;
        val rpleSubChunk = rpleSubChunkRoot.rpleSubChunk(channel);
        val hasSky = !chunk.worldObj.provider.hasNoSky;

        safeNibbleArray(section, "BlockLight", rpleSubChunk.blockLight());
        if (hasSky)
            safeNibbleArray(section, "SkyLight", rpleSubChunk.skyLight());
    }

    private static void safeNibbleArray(NBTTagCompound compound, String key, NibbleArray array) {
        if (!compound.hasKey(key, Constants.NBT.TAG_BYTE_ARRAY))
            return;

        val arr = compound.getByteArray(key);
        if (arr.length == 2048)
            System.arraycopy(arr, 0, array.data, 0, 2048);
    }

    @Override
    public String domain() {
        return Tags.MODID + "_" + Tags.VERSION;
    }

    @Override
    public String id() {
        return channel.name();
    }
}
