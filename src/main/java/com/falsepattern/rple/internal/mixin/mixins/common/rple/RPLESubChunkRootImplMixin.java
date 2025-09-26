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

import com.falsepattern.lumi.api.chunk.LumiSubChunk;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunk;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkContainer;
import com.falsepattern.rple.internal.common.chunk.RPLESubChunkRoot;
import lombok.val;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.falsepattern.lumi.api.init.LumiExtendedBlockStorageInitHook.LUMI_EXTENDED_BLOCK_STORAGE_INIT_HOOK_INFO;
import static com.falsepattern.lumi.api.init.LumiExtendedBlockStorageInitHook.LUMI_EXTENDED_BLOCK_STORAGE_INIT_HOOK_METHOD;
import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.mixin.Mixin.POST_LUMI_MIXIN_PRIORITY;

@Unique
@Mixin(value = ExtendedBlockStorage.class, priority = POST_LUMI_MIXIN_PRIORITY)
public abstract class RPLESubChunkRootImplMixin implements LumiSubChunk, RPLESubChunkRoot {
    @Shadow
    @Nullable
    private NibbleArray skylightArray;

    private RPLESubChunk rple$redChannel;
    private RPLESubChunk rple$greenChannel;
    private RPLESubChunk rple$blueChannel;

    @Inject(method = LUMI_EXTENDED_BLOCK_STORAGE_INIT_HOOK_METHOD,
            at = @At("RETURN"),
            remap = false,
            require = 1)
    @Dynamic(LUMI_EXTENDED_BLOCK_STORAGE_INIT_HOOK_INFO)
    private void rpleSubChunkInit(CallbackInfo ci) {
        val hasSky = skylightArray != null;
        this.rple$redChannel = new RPLESubChunkContainer(RED_CHANNEL, this, hasSky);
        this.rple$greenChannel = new RPLESubChunkContainer(GREEN_CHANNEL, this, hasSky);
        this.rple$blueChannel = new RPLESubChunkContainer(BLUE_CHANNEL, this, hasSky);
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
