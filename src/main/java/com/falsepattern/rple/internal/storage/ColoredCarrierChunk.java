package com.falsepattern.rple.internal.storage;

import net.minecraft.world.World;

public interface ColoredCarrierChunk {
    ColoredLightChunk getColoredChunk(int colorChannel);
}
