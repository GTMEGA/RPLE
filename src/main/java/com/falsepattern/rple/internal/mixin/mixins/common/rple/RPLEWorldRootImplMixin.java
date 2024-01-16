/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumina.api.LumiAPI;
import com.falsepattern.lumina.api.cache.LumiBlockCacheRoot;
import com.falsepattern.lumina.api.chunk.LumiChunkRoot;
import com.falsepattern.lumina.api.world.LumiWorld;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.common.cache.MultiHeadBlockCacheRoot;
import com.falsepattern.rple.internal.common.cache.RPLEBlockCacheRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import com.falsepattern.rple.internal.common.world.RPLEWorldContainer;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumina.api.init.LumiWorldInitHook.LUMI_WORLD_INIT_HOOK_INFO;
import static com.falsepattern.lumina.api.init.LumiWorldInitHook.LUMI_WORLD_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.common.cache.BlockCaches.createFallbackBlockCacheRoot;
import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.POST_LUMINA_MIXIN_PRIORITY;

@Unique
@Mixin(value = World.class, priority = POST_LUMINA_MIXIN_PRIORITY)
public abstract class RPLEWorldRootImplMixin implements IBlockAccess, LumiWorld, RPLEWorldRoot {
    @Shadow
    protected IChunkProvider chunkProvider;

    @Final
    @Shadow
    public Profiler theProfiler;

    @Dynamic
    @SuppressWarnings("unused")
    private LumiBlockCacheRoot lumi$blockCacheRoot;

    private RPLEBlockCacheRoot rple$blockCacheRoot;

    private RPLEWorld rple$redChannel;
    private RPLEWorld rple$greenChannel;
    private RPLEWorld rple$blueChannel;

    @Inject(method = LUMI_WORLD_INIT_HOOK_METHOD,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_WORLD_INIT_HOOK_INFO)
    private void rpleWorldInit(CallbackInfo ci) {
        int cacheCount = LumiAPI.configuredCacheCount();

        if (cacheCount <= 0 || (lumi$isClientSide() && Compat.falseTweaksThreadedChunksEnabled())) {
            this.rple$blockCacheRoot = createFallbackBlockCacheRoot(this);
        } else {
            this.rple$blockCacheRoot = new MultiHeadBlockCacheRoot(this, cacheCount);
        }
        this.lumi$blockCacheRoot = rple$blockCacheRoot;

        this.rple$redChannel = new RPLEWorldContainer(RED_CHANNEL, thiz(), this, theProfiler);
        this.rple$greenChannel = new RPLEWorldContainer(GREEN_CHANNEL, thiz(), this, theProfiler);
        this.rple$blueChannel = new RPLEWorldContainer(BLUE_CHANNEL, thiz(), this, theProfiler);
    }

    @Override
    public @Nullable RPLEChunkRoot rple$getChunkRootFromBlockPosIfExists(int posX, int posZ) {
        val chunkPosX = posX >> 4;
        val chunkPosZ = posZ >> 4;
        return rple$getChunkRootFromChunkPosIfExists(chunkPosX, chunkPosZ);
    }

    @Override
    public @Nullable RPLEChunkRoot rple$getChunkRootFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        if (chunkProvider instanceof ChunkProviderServer) {
            val chunkProviderServer = (ChunkProviderServer) chunkProvider;
            val loadedChunks = chunkProviderServer.loadedChunkHashMap;
            val chunk = loadedChunks.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(chunkPosX, chunkPosZ));
            if (chunk instanceof LumiChunkRoot)
                return (RPLEChunkRoot) chunk;
        }
        if (chunkProvider.chunkExists(chunkPosX, chunkPosZ)) {
            val chunk = chunkProvider.provideChunk(chunkPosX, chunkPosZ);
            if (chunk instanceof LumiChunkRoot)
                return (RPLEChunkRoot) chunk;
        }
        return null;
    }

    @Override
    public @NotNull RPLEBlockCacheRoot rple$blockCacheRoot() {
        return rple$blockCacheRoot;
    }

    @Override
    public @NotNull RPLEWorld rple$world(@NotNull ColorChannel channel) {
        return rple$blockStorage(channel);
    }

    @Override
    public @NotNull RPLEWorld rple$blockStorage(@NotNull ColorChannel channel) {
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

    private World thiz() {
        return (World) (Object) this;
    }
}
