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

package com.falsepattern.rple.internal.common.event;

import com.falsepattern.rple.api.client.lightmap.RPLELightMapRegistry;
import com.falsepattern.rple.api.common.colorizer.RPLEBlockColorRegistry;
import com.falsepattern.rple.api.common.event.BlockColorRegistrationEvent;
import com.falsepattern.rple.api.common.event.LightMapRegistrationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class EventPoster {
    public static void postLightMapRegistrationEvent(RPLELightMapRegistry registry) {
        val evt = new LightMapRegistrationEvent(registry);
        fmlCommonBus().post(evt);
    }

    public static void postBlockColorRegistrationEvent(RPLEBlockColorRegistry registry) {
        val evt = new BlockColorRegistrationEvent(registry);
        fmlCommonBus().post(evt);
    }

    private static EventBus fmlCommonBus() {
        return FMLCommonHandler.instance().bus();
    }
}
