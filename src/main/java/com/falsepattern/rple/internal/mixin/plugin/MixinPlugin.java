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

package com.falsepattern.rple.internal.mixin.plugin;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.IMixinPlugin;
import com.falsepattern.lib.mixin.ITargetedMod;
import com.falsepattern.rple.internal.common.util.LogHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;

@NoArgsConstructor
public final class MixinPlugin implements IMixinPlugin {
    public static final int RPLE_INIT_MIXIN_PRIORITY = 980;
    public static final int RPLE_ROOT_MIXIN_PRIORITY = 990;
    public static final int POST_LUMI_MIXIN_PRIORITY = 1010;

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

    @Override
    public boolean useNewFindJar() {
        return true;
    }
}
