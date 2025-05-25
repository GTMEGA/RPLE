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

package com.falsepattern.rple.internal.mixin.mixins.client.fairylights;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.internal.mixin.extension.ExtendedOpenGlHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.pau101.fairylights.client.model.connection.ModelConnection;
import com.pau101.fairylights.connection.ConnectionLogic;
import com.pau101.fairylights.util.Catenary;
import com.pau101.fairylights.util.Segment;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.world.World;

@Mixin(value = ModelConnection.class,
       remap = false)
public abstract class ModelConnectionMixin<T extends ConnectionLogic> extends ModelBase {
    @Redirect(method = "renderCord",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/OpenGlHelper;setLightmapTextureCoords(IFF)V",
                       remap = true),
              require = 1)
    private void noNaiveInterpolate(int target, float x, float y) {

    }

    @Inject(method = "renderCord",
            at = @At("HEAD"),
            remap = false,
            require = 1)
    private void smartInterpolate1(T connectionLogic, World world, int sunlight, int moonlight, float delta, CallbackInfo ci, @Share("from") LocalLongRef ref) {
        long from = CookieMonster.RGB64FromCookie(sunlight | (moonlight << 16));
        ref.set(from);
    }

    @WrapOperation(method = "renderCord",
                   at = @At(value = "INVOKE",
                            target = "Lnet/minecraft/world/World;getLightBrightnessForSkyBlocks(IIII)I",
                            remap = true),
                   require = 1)
    private int smartInterpolate2(World instance, int x, int y, int z, int min, Operation<Integer> original, @Share("to")LocalLongRef ref) {
        int cookie = original.call(instance, x, y, z, min);
        ref.set(CookieMonster.RGB64FromCookie(cookie));
        return cookie;
    }

    @WrapOperation(method = "renderCord",
                   at = @At(value = "INVOKE",
                            target = "Lcom/pau101/fairylights/util/Catenary;getSegments()[Lcom/pau101/fairylights/util/Segment;"),
                   remap = false,
                   require = 1)
    private Segment[] smartInterpolate3(Catenary instance, Operation<Segment[]> original, @Share("length")LocalIntRef ref, @Share("idx") LocalIntRef idx) {
        val segments = original.call(instance);
        ref.set(segments.length);
        idx.set(0);
        return segments;
    }

    @Inject(method = "renderCord",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/OpenGlHelper;setLightmapTextureCoords(IFF)V",
                     remap = true),
            require = 1)
    private void smartInterpolate4(T connectionLogic, World world, int sunlight, int moonlight, float delta, CallbackInfo ci,
                                  @Share("from") LocalLongRef fromR,
                                  @Share("to") LocalLongRef toR,
                                  @Share("length") LocalIntRef lengthR,
                                  @Share("idx") LocalIntRef idxR) {
        long from = fromR.get();
        long to = toR.get();
        int length = lengthR.get();
        int idx = idxR.get();
        idxR.set(idx + 1);
        float v = (float) idx / (float) length;
        long mixed = ClientColorHelper.RGB64MixAOBrightness(from, to, 1f - v, v);
        ExtendedOpenGlHelper.setLightMapTextureCoordsRGB64(mixed);
    }
}
