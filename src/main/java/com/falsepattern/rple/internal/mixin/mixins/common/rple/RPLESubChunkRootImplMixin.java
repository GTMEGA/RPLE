/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.chunk.LumiSubChunk;
import com.falsepattern.rple.api.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.RPLESubChunk;
import com.falsepattern.rple.internal.common.storage.RPLESubChunkRoot;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExtendedBlockStorage.class)
public abstract class RPLESubChunkRootImplMixin implements LumiSubChunk, RPLESubChunkRoot {
    private RPLESubChunk rple$redChannel = null;
    private RPLESubChunk rple$greenChannel = null;
    private RPLESubChunk rple$blueChannel = null;

    @Inject(method = "<init>*",
            at = @At("RETURN"),
            require = 1)
    private void rpleSubChunkRootInit(int posY, boolean hasSky, CallbackInfo ci) {
        this.rple$redChannel = new RPLESubChunk();
        this.rple$greenChannel = new RPLESubChunk();
        this.rple$blueChannel = new RPLESubChunk();
    }

    @Override
    public RPLESubChunk rple$subChunk(ColorChannel channel) {
        switch (channel) {
            default:
            case RED_CHANNEL:
                return rple$redChannel;
            case GREEN_CHANNEL:
                return rple$greenChannel;
            case BLUE_CHANNEL:
                return rple$blueChannel;
        }
    }
}
