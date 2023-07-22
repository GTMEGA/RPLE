/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.common.event;

import com.falsepattern.rple.api.common.block.RPLEBlockColorRegistry;
import cpw.mods.fml.common.eventhandler.Event;

public final class BlockColorRegistrationEvent extends Event {
    private final RPLEBlockColorRegistry registry;

    public BlockColorRegistrationEvent(RPLEBlockColorRegistry registry) {
        this.registry = registry;
    }

    public RPLEBlockColorRegistry registry() {
        return this.registry;
    }
}
