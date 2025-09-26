/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumi.api.chunk.LumiChunkRoot;
import com.falsepattern.lumi.api.world.LumiWorld;
import com.falsepattern.rple.api.common.color.ColorChannel;
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

import static com.falsepattern.lumi.api.init.LumiWorldInitHook.LUMI_WORLD_INIT_HOOK_INFO;
import static com.falsepattern.lumi.api.init.LumiWorldInitHook.LUMI_WORLD_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.mixin.Mixin.POST_LUMI_MIXIN_PRIORITY;

@Unique
@Mixin(value = World.class, priority = POST_LUMI_MIXIN_PRIORITY)
public abstract class RPLEWorldRootImplMixin implements IBlockAccess, LumiWorld, RPLEWorldRoot {
    @Shadow
    protected IChunkProvider chunkProvider;

    @Final
    @Shadow
    public Profiler theProfiler;

    private RPLEWorld rple$redChannel;
    private RPLEWorld rple$greenChannel;
    private RPLEWorld rple$blueChannel;

    @Inject(method = LUMI_WORLD_INIT_HOOK_METHOD,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_WORLD_INIT_HOOK_INFO)
    private void rpleWorldInit(CallbackInfo ci) {
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

    // TODO: Will this break when using RPLE Threading? Do we even need this?
    //
    // If you do fix it, Lumi has nearly the same code, fix it there too tbh.
    @Override
    public @Nullable RPLEChunkRoot rple$getChunkRootFromChunkPosIfExists(int chunkPosX, int chunkPosZ) {
        if (chunkProvider == null)
            return null;
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
