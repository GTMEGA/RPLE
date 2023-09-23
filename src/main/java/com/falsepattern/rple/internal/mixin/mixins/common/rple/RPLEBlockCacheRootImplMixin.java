package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.storage.RPLEBlockCache;
import com.falsepattern.rple.internal.common.storage.RPLEBlockCacheContainer;
import com.falsepattern.rple.internal.common.storage.RPLEBlockCacheRoot;
import com.falsepattern.rple.internal.common.storage.RPLEBlockStorage;
import com.falsepattern.rple.internal.common.world.RPLEWorldRoot;
import lombok.val;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumina.api.init.LumiChunkCacheInitHook.LUMI_CHUNK_CACHE_INIT_HOOK_INFO;
import static com.falsepattern.lumina.api.init.LumiChunkCacheInitHook.LUMI_CHUNK_CACHE_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@Unique
@Mixin(ChunkCache.class)
public abstract class RPLEBlockCacheRootImplMixin implements IBlockAccess, RPLEBlockCacheRoot {
    @Shadow
    public World worldObj;

    private RPLEBlockCache rple$redChannel;
    private RPLEBlockCache rple$greenChannel;
    private RPLEBlockCache rple$blueChannel;

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
        this.rple$redChannel = new RPLEBlockCacheContainer(RED_CHANNEL, thiz(), worldRoot, this);
        this.rple$greenChannel = new RPLEBlockCacheContainer(GREEN_CHANNEL, thiz(), worldRoot, this);
        this.rple$blueChannel = new RPLEBlockCacheContainer(BLUE_CHANNEL, thiz(), worldRoot, this);
    }

    @Override
    public @NotNull RPLEBlockStorage rple$blockStorage(@NotNull ColorChannel channel) {
        return rple$blockCache(channel);
    }

    @Override
    public @NotNull RPLEBlockCache rple$blockCache(@NotNull ColorChannel channel) {
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

    private ChunkCache thiz() {
        return (ChunkCache) (Object) this;
    }
}
