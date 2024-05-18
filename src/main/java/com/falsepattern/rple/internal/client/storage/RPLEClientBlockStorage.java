/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.storage;

import com.falsepattern.lumina.api.lighting.LightType;
import net.minecraft.block.Block;

public interface RPLEClientBlockStorage {
    /**
     * @implSpec useNeighborValues should get it's value from: {@link Block#getUseNeighborBrightness()}
     */
    int rple$getRGBLightValue(boolean useNeighborValues, int posX, int posY, int posZ);

    int rple$getRGBLightValue(boolean useNeighborValues, LightType type, int posX, int posY, int posZ);
}
