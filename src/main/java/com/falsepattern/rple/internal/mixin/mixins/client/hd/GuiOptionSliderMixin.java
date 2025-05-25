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

package com.falsepattern.rple.internal.mixin.mixins.client.hd;

import com.falsepattern.lib.util.MathUtil;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import lombok.var;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;

@Mixin(GuiOptionSlider.class)
public abstract class GuiOptionSliderMixin extends GuiButton {
    @Shadow private float field_146134_p;

    public GuiOptionSliderMixin(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    @Inject(method = "<init>(IIILnet/minecraft/client/settings/GameSettings$Options;FF)V",
            at = @At("RETURN"),
            require = 0)
    private void disableIfGamma(int buttonId, int x, int y, GameSettings.Options option, float minValue, float maxValue, CallbackInfo ci) {
        if (option == GameSettings.Options.GAMMA) {
            var gammaOverride = RPLEConfig.HD.GAMMA_OVERRIDE;

            if (gammaOverride != -1) {
                gammaOverride = MathUtil.clamp(gammaOverride, 0, 1);
                this.enabled = false;
                this.field_146134_p = (float) gammaOverride;
            }
        }
    }
}
