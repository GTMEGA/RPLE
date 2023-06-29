/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.event;

import com.falsepattern.rple.api.block.BlockColorRegistry;
import com.falsepattern.rple.api.event.BlockColorRegistrationEvent;
import com.falsepattern.rple.api.event.LightMapRegistrationEvent;
import com.falsepattern.rple.api.lightmap.LightMapRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public final class EventPoster {
    public static void postLightMapRegistrationEvent(LightMapRegistry registry) {
        val evt = new LightMapRegistrationEvent(registry);
        fmlCommonBus().post(evt);
    }

    public static void postBlockColorRegistrationEvent(BlockColorRegistry registry) {
        val evt = new BlockColorRegistrationEvent(registry);
        fmlCommonBus().post(evt);
    }

    private static EventBus fmlCommonBus() {
        return FMLCommonHandler.instance().bus();
    }
}
