/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.event;

import com.falsepattern.rple.api.client.lightmap.RPLELightMapRegistry;
import cpw.mods.fml.common.eventhandler.Event;

public final class LightMapRegistrationEvent extends Event {
    private final RPLELightMapRegistry registry;

    public LightMapRegistrationEvent(RPLELightMapRegistry registry) {
        this.registry = registry;
    }

    public RPLELightMapRegistry registry() {
        return this.registry;
    }
}
