package com.falsepattern.rple.internal.storage;

public interface ColoredBlock {
    int getColoredLightValue(ColoredLightWorld world, int x, int y, int z);
    int getColoredLightOpacity(ColoredLightWorld world, int x, int y, int z);
}
