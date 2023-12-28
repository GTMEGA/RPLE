/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.chunk;

import com.falsepattern.chunk.api.ArrayUtil;
import com.falsepattern.lumina.api.chunk.LumiSubChunk;
import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.NibbleArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static com.falsepattern.rple.internal.Tags.MOD_ID;

@Getter
@Accessors(fluent = true, chain = false)
public final class RPLESubChunkContainer implements RPLESubChunk {
    private final ColorChannel channel;
    private final String subChunkID;
    private final RPLESubChunkRoot root;

    private NibbleArray blockLight;
    @Nullable
    private NibbleArray skyLight;

    public RPLESubChunkContainer(ColorChannel channel, RPLESubChunkRoot root, boolean hasSky) {
        this.channel = channel;
        this.subChunkID = MOD_ID + "_" + channel + "_sub_chunk";
        this.root = root;

        this.blockLight = new NibbleArray(4096, 4);
        if (hasSky) {
            this.skyLight = new NibbleArray(4096, 4);
        } else {
            this.skyLight = null;
        }
    }

    @Override
    public @NotNull ColorChannel rple$channel() {
        return channel;
    }

    @Override
    public @NotNull RPLESubChunkRoot lumi$root() {
        return root;
    }

    @Override
    public @NotNull String lumi$subChunkID() {
        return subChunkID;
    }

    @Override
    public void lumi$writeToNBT(@NotNull NBTTagCompound output) {
        output.setByteArray(BLOCK_LIGHT_NBT_TAG_NAME, blockLight.data);
        if (skyLight != null)
            output.setByteArray(SKY_LIGHT_NBT_TAG_NAME, skyLight.data);
    }

    @Override
    public void lumi$readFromNBT(@NotNull NBTTagCompound input) {
        if (input.hasKey(BLOCK_LIGHT_NBT_TAG_NAME, 7)) {
            val blockLightBytes = input.getByteArray(BLOCK_LIGHT_NBT_TAG_NAME);
            if (blockLightBytes.length == 2048)
                System.arraycopy(blockLightBytes, 0, blockLight.data, 0, 2048);
        }

        if (skyLight != null && input.hasKey(SKY_LIGHT_NBT_TAG_NAME, 7)) {
            val skyLightBytes = input.getByteArray(SKY_LIGHT_NBT_TAG_NAME);
            if (skyLightBytes.length == 2048)
                System.arraycopy(skyLightBytes, 0, skyLight.data, 0, 2048);
        }
    }

    @Override
    public void lumi$cloneFrom(LumiSubChunk from) {
        blockLight = ArrayUtil.copyArray(from.lumi$getBlockLightArray(), lumi$getBlockLightArray());
        skyLight = ArrayUtil.copyArray(from.lumi$getSkyLightArray(), lumi$getSkyLightArray());
    }

    @Override
    public void lumi$writeToPacket(@NotNull ByteBuffer output) {
        output.put(blockLight.data);
        if (skyLight != null)
            output.put(skyLight.data);
    }

    @Override
    public void lumi$readFromPacket(@NotNull ByteBuffer input) {
        input.get(blockLight.data);
        if (skyLight != null)
            input.get(skyLight.data);
    }

    @Override
    public void lumi$setLightValue(@NotNull LightType lightType,
                                   int subChunkPosX,
                                   int subChunkPosY,
                                   int subChunkPosZ,
                                   int lightValue) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                lumi$setBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
                break;
            case SKY_LIGHT_TYPE:
                lumi$setSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
                break;
            default:
                break;
        }
    }

    @Override
    public int lumi$getLightValue(@NotNull LightType lightType, int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        switch (lightType) {
            case BLOCK_LIGHT_TYPE:
                return lumi$getBlockLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
            case SKY_LIGHT_TYPE:
                return lumi$getSkyLightValue(subChunkPosX, subChunkPosY, subChunkPosZ);
            default:
                return lightType.defaultLightValue();
        }
    }

    @Override
    public void lumi$setBlockLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ, int lightValue) {
        blockLight.set(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
    }

    @Override
    public int lumi$getBlockLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return blockLight.get(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public void lumi$setSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ, int lightValue) {
        if (skyLight != null)
            skyLight.set(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
    }

    @Override
    public int lumi$getSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        if (skyLight != null)
            return skyLight.get(subChunkPosX, subChunkPosY, subChunkPosZ);
        return 0;
    }

    @Override
    public NibbleArray lumi$getBlockLightArray() {
        return blockLight;
    }

    @Override
    public NibbleArray lumi$getSkyLightArray() {
        return skyLight;
    }
}
