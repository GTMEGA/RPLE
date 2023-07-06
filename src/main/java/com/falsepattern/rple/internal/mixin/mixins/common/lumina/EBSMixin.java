/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.chunk.LumiSubChunk;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.RPLESubChunk;
import com.falsepattern.rple.internal.common.storage.RPLESubChunkRoot;
import lombok.val;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(ExtendedBlockStorage.class)
public abstract class EBSMixin implements LumiSubChunk, RPLESubChunkRoot {
    @Shadow
    private NibbleArray blocklightArray;
    @Shadow
    private NibbleArray skylightArray;

    @Nullable
    private RPLESubChunk redChannel = null;
    @Nullable
    private RPLESubChunk greenChannel = null;
    @Nullable
    private RPLESubChunk blueChannel = null;

    private boolean colorInit;

    @Override
    public RPLESubChunk rpleSubChunk(ColorChannel channel) {
        if (!colorInit) {
            val hasSky = skylightArray != null;
            rpleSubChunkInit(hasSky);
            colorInit = true;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                return redChannel;
            case GREEN_CHANNEL:
                return greenChannel;
            case BLUE_CHANNEL:
                return blueChannel;
        }
    }

    private void rpleSubChunkInit(boolean hasSky) {
        this.redChannel = new RPLESubChunk(this, blocklightArray, skylightArray);
        this.greenChannel = new RPLESubChunk(this, hasSky);
        this.blueChannel = new RPLESubChunk(this, hasSky);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtSkylightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        val redLightArray = rpleSubChunk(RED_CHANNEL).skyLight();
        val greenLightArray = rpleSubChunk(GREEN_CHANNEL).skyLight();
        val blueLightArray = rpleSubChunk(BLUE_CHANNEL).skyLight();

        return maxLightValue(redLightArray, greenLightArray, blueLightArray, subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    /**
     * @author FalsePattern
     * @reason Logic compat
     */
    @Overwrite
    public int getExtBlocklightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        val redLightArray = rpleSubChunk(RED_CHANNEL).blockLight();
        val greenLightArray = rpleSubChunk(GREEN_CHANNEL).blockLight();
        val blueLightArray = rpleSubChunk(BLUE_CHANNEL).blockLight();

        return maxLightValue(redLightArray, greenLightArray, blueLightArray, subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    private static int maxLightValue(NibbleArray redLightArray,
                                     NibbleArray greenLightArray,
                                     NibbleArray blueLightArray,
                                     int subChunkPosX,
                                     int subChunkPosY,
                                     int subChunkPosZ) {
        val redLightValue = redLightArray.get(subChunkPosX, subChunkPosY, subChunkPosZ);
        val greenLightValue = greenLightArray.get(subChunkPosX, subChunkPosY, subChunkPosZ);
        val blueLightValue = blueLightArray.get(subChunkPosX, subChunkPosY, subChunkPosZ);
        return Math.max(redLightValue, Math.max(greenLightValue, blueLightValue));
    }
}
