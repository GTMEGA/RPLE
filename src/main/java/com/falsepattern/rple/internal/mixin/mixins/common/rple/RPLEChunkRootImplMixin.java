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

package com.falsepattern.rple.internal.mixin.mixins.common.rple;

import com.falsepattern.lumi.api.chunk.LumiChunk;
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

import static com.falsepattern.lumi.api.init.LumiChunkInitHook.LUMI_CHUNK_INIT_HOOK_INFO;
import static com.falsepattern.lumi.api.init.LumiChunkInitHook.LUMI_CHUNK_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.mixin.plugin.MixinPlugin.POST_LUMI_MIXIN_PRIORITY;

@Unique
@Mixin(value = Chunk.class, priority = POST_LUMI_MIXIN_PRIORITY)
public abstract class RPLEChunkRootImplMixin implements LumiChunk, RPLEChunkRoot {
    @Shadow
    public World worldObj;

    private RPLEChunk rple$redChannel;
    private RPLEChunk rple$greenChannel;
    private RPLEChunk rple$blueChannel;

    @Inject(method = LUMI_CHUNK_INIT_HOOK_METHOD,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_CHUNK_INIT_HOOK_INFO)
    @SuppressWarnings("CastToIncompatibleInterface")
    private void rpleChunkInit(CallbackInfo ci) {
        if (this.worldObj == null)
            return;

        val worldRoot = (RPLEWorldRoot) this.worldObj;
        this.rple$redChannel = new RPLEChunkContainer(RED_CHANNEL, worldRoot, this, this);
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
