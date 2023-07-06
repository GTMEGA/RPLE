/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.RPLEChunk;
import com.falsepattern.rple.internal.common.storage.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.storage.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.falsepattern.rple.api.color.ColorChannel.*;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements LumiChunk, RPLEChunkRoot {
    @Shadow
    public World worldObj;
    @Shadow
    public int[] heightMap;
    @Shadow
    public boolean[] updateSkylightColumns;

    @Nullable
    private RPLEChunk redChannel = null;
    @Nullable
    private RPLEChunk greenChannel = null;
    @Nullable
    private RPLEChunk blueChannel = null;

    private boolean colorInit = false;

    @Override
    public RPLEChunk rpleChunk(ColorChannel channel) {
        if (!colorInit) {
            rpleChunkInit();
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

    private void rpleChunkInit() {
        val rpleWorldRoot = (RPLEWorldRoot) worldObj;
        this.redChannel = new RPLEChunk(this, rpleWorldRoot.rpleWorld(RED_CHANNEL), heightMap, updateSkylightColumns);
        this.greenChannel = new RPLEChunk(this, rpleWorldRoot.rpleWorld(GREEN_CHANNEL));
        this.blueChannel = new RPLEChunk(this, rpleWorldRoot.rpleWorld(BLUE_CHANNEL));
    }
}
