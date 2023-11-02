/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.api.common.lamp.LampBlock;
import com.falsepattern.rple.internal.Tags;
import lombok.val;

public class RPLELampBlock extends LampBlock {
    public RPLELampBlock(Lamps lamp) {
        super(lamp.r, lamp.g, lamp.b);
        val name = lamp.name().toLowerCase();
        this.setBlockName(Tags.MOD_ID + ".lamp." + name);
        this.setBlockTextureName(name);
    }

    @Override
    protected String getOffTextureName() {
        return Tags.MOD_ID + ":lamp/off/" + getTextureName();
    }

    @Override
    protected String getOnTextureName() {
        return Tags.MOD_ID + ":lamp/on/" + getTextureName();
    }
}
