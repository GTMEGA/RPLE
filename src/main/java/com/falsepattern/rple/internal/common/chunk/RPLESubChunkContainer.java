/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.chunk;

import com.falsepattern.chunk.api.ArrayUtil;
import com.falsepattern.lumi.api.chunk.LumiSubChunk;
import com.falsepattern.lumi.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.ArrayHelper;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.NibbleArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraftforge.common.util.Constants;

import java.nio.ByteBuffer;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.Tags.MOD_ID;

@Getter
@Accessors(fluent = true, chain = false)
public final class RPLESubChunkContainer implements RPLESubChunk {
    private static final String RED_SUB_CHUNK_ID = MOD_ID + "_" + RED_CHANNEL + "_sub_chunk";
    private static final String GREEN_SUB_CHUNK_ID = MOD_ID + "_" + GREEN_CHANNEL + "_sub_chunk";
    private static final String BLUE_SUB_CHUNK_ID = MOD_ID + "_" + BLUE_CHANNEL + "_sub_chunk";

    private final ColorChannel channel;
    private final RPLESubChunkRoot root;

    @Nullable
    private NibbleArray blockLight;
    @Nullable
    private NibbleArray skyLight;

    public RPLESubChunkContainer(ColorChannel channel, RPLESubChunkRoot root, boolean hasSky) {
        this.channel = channel;
        this.root = root;
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
        switch (channel) {
            default:
            case RED_CHANNEL:
                return RED_SUB_CHUNK_ID;
            case GREEN_CHANNEL:
                return GREEN_SUB_CHUNK_ID;
            case BLUE_CHANNEL:
                return BLUE_SUB_CHUNK_ID;
        }
    }

    @Override
    public void lumi$writeToNBT(@NotNull NBTTagCompound output) {
        if (blockLight != null)
            output.setByteArray(BLOCK_LIGHT_NBT_TAG_NAME, blockLight.data);
        if (skyLight != null)
            output.setByteArray(SKY_LIGHT_NBT_TAG_NAME, skyLight.data);
    }

    @Override
    public void lumi$readFromNBT(@NotNull NBTTagCompound input) {
        if (input.hasKey(BLOCK_LIGHT_NBT_TAG_NAME, Constants.NBT.TAG_BYTE_ARRAY)) {
            val blockLightBytes = input.getByteArray(BLOCK_LIGHT_NBT_TAG_NAME);
            if (ArrayHelper.isZero(blockLightBytes)) {
                blockLight = null;
            } else if (blockLight == null) {
                blockLight = new NibbleArray(blockLightBytes, 4);
            } else {
                System.arraycopy(blockLightBytes, 0, blockLight.data, 0, 2048);
            }
        } else {
            blockLight = null;
        }

        if (input.hasKey(SKY_LIGHT_NBT_TAG_NAME, Constants.NBT.TAG_BYTE_ARRAY)) {
            val skyLightBytes = input.getByteArray(SKY_LIGHT_NBT_TAG_NAME);
            if (ArrayHelper.isZero(skyLightBytes)) {
                skyLight = null;
            } else if (skyLight == null) {
                skyLight = new NibbleArray(skyLightBytes, 4);
            } else {
                System.arraycopy(skyLightBytes, 0, skyLight.data, 0, 2048);
            }
        } else {
            skyLight = null;
        }
    }

    @Override
    public void lumi$cloneFrom(LumiSubChunk from) {
        blockLight = ArrayUtil.copyArray(from.lumi$getBlockLightArray(), lumi$getBlockLightArray());
        skyLight = ArrayUtil.copyArray(from.lumi$getSkyLightArray(), lumi$getSkyLightArray());
    }

    @Override
    public void lumi$writeToPacket(@NotNull ByteBuffer output) {
        if (blockLight != null && ArrayHelper.isZero(blockLight.data))
            blockLight = null;
        if (skyLight != null && ArrayHelper.isZero(skyLight.data))
            skyLight = null;

        byte flag = (byte) ((blockLight != null ? 1 : 0) | (skyLight != null ? 2 : 0));
        output.put(flag);
        if (blockLight != null)
            output.put(blockLight.data);
        if (skyLight != null)
            output.put(skyLight.data);
    }

    @Override
    public void lumi$readFromPacket(@NotNull ByteBuffer input) {
        byte flag = input.get();
        boolean doBlock = (flag & 1) != 0;
        boolean doSky = (flag & 2) != 0;

        if (doBlock) {
            if (blockLight == null) {
                blockLight = new NibbleArray(4096, 4);
            }
            input.get(blockLight.data);
        } else {
            blockLight = null;
        }
        if (doSky) {
            if (skyLight == null) {
                skyLight = new NibbleArray(4096, 4);
            }
            input.get(skyLight.data);
        } else {
            skyLight = null;
        }
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
        if (blockLight == null) {
            if (lightValue == 0)
                return;
            blockLight = new NibbleArray(4096, 4);
        }
        blockLight.set(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
    }

    @Override
    public int lumi$getBlockLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        if (blockLight == null)
            return 0;

        return blockLight.get(subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    @Override
    public void lumi$setSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ, int lightValue) {
        if (skyLight == null) {
            if (lightValue == 0)
                return;

            skyLight = new NibbleArray(4096, 4);
        }

        skyLight.set(subChunkPosX, subChunkPosY, subChunkPosZ, lightValue);
    }

    @Override
    public int lumi$getSkyLightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        if (skyLight == null)
            return 0;

        return skyLight.get(subChunkPosX, subChunkPosY, subChunkPosZ);
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
