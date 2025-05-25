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

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadersmod.client.ShaderMacros;

import static com.falsepattern.rple.internal.client.optifine.Constants.RPLE_API_VERSION;

@Mixin(value = ShaderMacros.class,
       remap = false)
public abstract class ShaderMacrosMixin {
    @Shadow private static void addMacroLine(StringBuilder sb, String name) {

    }

    @Shadow private static void addMacroLine(StringBuilder sb, String name, int value) {

    }

    @Inject(method = "getMacroLines",
            at = @At(value = "INVOKE",
                     target = "Lshadersmod/client/ShaderMacros;addMacroLine(Ljava/lang/StringBuilder;Ljava/lang/String;)V"),
            require = 1)
    private static void addRPLEMacros(CallbackInfoReturnable<String> cir,
                                      @Local StringBuilder sb) {
        addMacroLine(sb, "RPLE");
        addMacroLine(sb, "RPLE_API", RPLE_API_VERSION);
    }
}
