/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.IMixinPlugin;
import com.falsepattern.lib.mixin.ITargetedMod;
import com.falsepattern.rple.internal.LogHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@NoArgsConstructor
public final class MixinPlugin implements IMixinPlugin {
    public static final int RPLE_INIT_MIXIN_PRIORITY = 980;
    public static final int RPLE_ROOT_MIXIN_PRIORITY = 990;
    public static final int POST_LUMINA_MIXIN_PRIORITY = 1010;

    @Getter
    private final Logger logger = LogHelper.createLogger("Mixins");

    @Override
    public ITargetedMod[] getTargetedModEnumValues() {
        return TargetedMod.values();
    }

    @Override
    public IMixin[] getMixinEnumValues() {
        return Mixin.values();
    }
}
