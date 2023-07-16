/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.storage.chunk.RPLEChunkWrapper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Unique
@Mixin(Chunk.class)
public abstract class RPLEChunkRootImplMixin implements LumiChunk, RPLEChunkRoot {
    private RPLEChunk redChannel = null;
    private RPLEChunk greenChannel = null;
    private RPLEChunk blueChannel = null;

    @Inject(method = "<init>*",
            at = @At("RETURN"),
            require = 1)
    private void rpleChunkRootInit() {
        this.redChannel = new RPLEChunkWrapper();
        this.greenChannel = new RPLEChunkWrapper();
        this.blueChannel = new RPLEChunkWrapper();
    }

    @Override
    public RPLEChunk rple$chunk(ColorChannel channel) {
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
}
