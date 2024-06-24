package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorage;
import com.falsepattern.rple.internal.common.cache.RPLEBlockStorageRoot;
import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumi.api.init.LumiChunkCacheInitHook.LUMI_CHUNK_CACHE_INIT_HOOK_INFO;
import static com.falsepattern.lumi.api.init.LumiChunkCacheInitHook.LUMI_CHUNK_CACHE_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@Unique
@Mixin(ChunkCache.class)
public abstract class RPLEBlockCacheRootImplMixin implements IBlockAccess, RPLEBlockStorageRoot {
    @Shadow
    public World worldObj;

    private RPLEBlockStorage rple$redChannel;
    private RPLEBlockStorage rple$greenChannel;
    private RPLEBlockStorage rple$blueChannel;

    @Inject(method = LUMI_CHUNK_CACHE_INIT_HOOK_METHOD,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_CHUNK_CACHE_INIT_HOOK_INFO)
    @SuppressWarnings("CastToIncompatibleInterface")
    private void rpleBlockCacheRootInit(CallbackInfo ci) {
        if (this.worldObj == null)
            return;

        val worldRoot = (RPLEWorldRoot) worldObj;
        this.rple$redChannel = worldRoot.rple$world(RED_CHANNEL).getCloneForChunkCache(this);
        this.rple$greenChannel = worldRoot.rple$world(GREEN_CHANNEL).getCloneForChunkCache(this);
        this.rple$blueChannel = worldRoot.rple$world(BLUE_CHANNEL).getCloneForChunkCache(this);
    }

    @Override
    public @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel) {
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

    @Override
    public @Nullable RPLEChunkRoot rple$getChunkRootFromBlockPosIfExists(int posX, int posZ) {
        val chunk = lumi$getChunkRootFromBlockPosIfExists(posX, posZ);
        if (chunk instanceof RPLEChunkRoot)
            return (RPLEChunkRoot) chunk;
        return null;
    }

    @Override
    public @Nullable RPLEChunkRoot rple$getChunkRootFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        val chunk = lumi$getChunkRootFromChunkPosIfExists(chunkPosX, chunkPosZ);
        if (chunk instanceof RPLEChunkRoot)
            return (RPLEChunkRoot) chunk;
        return null;
    }

    private ChunkCache thiz() {
        return (ChunkCache) (Object) this;
    }
}
