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

package com.falsepattern.rple.internal.common.event;

import com.falsepattern.lumi.api.event.LumiWorldProviderRegistrationEvent;
import com.falsepattern.rple.internal.Tags;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Logger;

import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
import static com.falsepattern.rple.internal.common.world.RPLEWorldProvider.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LumiEventHandler {
    private static final Logger LOG = createLogger("Lumi Event Handler");

    private static final LumiEventHandler INSTANCE = new LumiEventHandler();

    private static final EventBus EVENT_BUS = FMLCommonHandler.instance().bus();

    private boolean isRegistered = false;

    public static LumiEventHandler lumiEventHandler() {
        return INSTANCE;
    }

    public void registerEventHandler() {
        if (isRegistered)
            return;
        EVENT_BUS.register(this);
        isRegistered = true;
        LOG.info("Registered event handler");
    }

    @SubscribeEvent
    public void lumiWorldRegistration(LumiWorldProviderRegistrationEvent evt) {
        val registry = evt.registry();
        registry.hijackDefaultWorldProviders(Tags.MOD_NAME);
        registry.registerWorldProvider(redRPLEWorldProvider());
        registry.registerWorldProvider(greenRPLEWorldProvider());
        registry.registerWorldProvider(blueRPLEWorldProvider());
        LOG.info("Registered RGB world providers");
    }
}
