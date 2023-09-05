/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.chunk.LumiChunk;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkContainer;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumina.api.init.LumiChunkBaseInit.LUMI_CHUNK_BASE_INIT_METHOD_REFERENCE;
import static com.falsepattern.lumina.api.init.LumiChunkBaseInit.LUMI_CHUNK_BASE_INIT_MIXIN_VALUE;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.POST_LUMINA_MIXIN_PRIORITY;

@Unique
@Mixin(value = Chunk.class, priority = POST_LUMINA_MIXIN_PRIORITY)
public abstract class RPLEChunkRootImplMixin implements LumiChunk, RPLEChunkRoot {
    @Shadow
    public boolean[] updateSkylightColumns;
    @Shadow
    public World worldObj;
    @Shadow
    public int[] heightMap;

    private RPLEChunk rple$redChannel;
    private RPLEChunk rple$greenChannel;
    private RPLEChunk rple$blueChannel;

    @Inject(method = LUMI_CHUNK_BASE_INIT_METHOD_REFERENCE,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_CHUNK_BASE_INIT_MIXIN_VALUE)
    @SuppressWarnings("CastToIncompatibleInterface")
    private void rpleChunkInit(CallbackInfo ci) {
        if (this.worldObj == null)
            return;

        val worldRoot = (RPLEWorldRoot) this.worldObj;

        this.rple$redChannel = new RPLEChunkContainer(RED_CHANNEL,
                                                      worldRoot,
                                                      this,
                                                      this,
                                                      heightMap,
                                                      updateSkylightColumns);
        this.rple$greenChannel = new RPLEChunkContainer(GREEN_CHANNEL, worldRoot, this, this);
        this.rple$blueChannel = new RPLEChunkContainer(BLUE_CHANNEL, worldRoot, this, this);
    }

    @Override
    public RPLEChunk rple$chunk(ColorChannel channel) {
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
