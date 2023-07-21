/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.event;

import com.falsepattern.lumina.api.event.LumiWorldProviderRegistrationEvent;
import com.falsepattern.rple.internal.Tags;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Logger;

import static com.falsepattern.rple.internal.RightProperLightingEngine.createLogger;
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
