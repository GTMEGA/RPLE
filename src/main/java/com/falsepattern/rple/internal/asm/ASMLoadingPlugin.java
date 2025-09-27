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

package com.falsepattern.rple.internal.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.falsepattern.rple.internal.mixin.Mixin;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.falsepattern.rple.internal.Tags.MOD_NAME;
import static cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;

@Name(MOD_NAME + "|ASM Plugin")
@MCVersion("1.7.10")
@SortingIndex(Integer.MAX_VALUE)
@TransformerExclusions({"com.falsepattern.lumi.internal.asm","com.falsepattern.rple.internal.asm"})
@NoArgsConstructor
public final class ASMLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        val mixinTweakClasses = GlobalProperties.<List<String>>get(MixinServiceLaunchWrapper.BLACKBOARD_KEY_TWEAKCLASSES);
        if (mixinTweakClasses != null) {
            mixinTweakClasses.add("com.falsepattern.rple.internal.asm.MixinCompatHackTweaker");
        }
        if (RPLEConfig.Compat.FASTER_GL_STATE_TRACKING) {
            return new String[]{"com.falsepattern.rple.internal.asm.TextureStateTrackerInjector"};
        }
        return new String[0];
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.rple.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixin.class, loadedCoreMods);
    }
}
