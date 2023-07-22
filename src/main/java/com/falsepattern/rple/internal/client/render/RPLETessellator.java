package com.falsepattern.rple.internal.client.render;

import net.minecraft.client.renderer.Tessellator;

public interface RPLETessellator {
    void rple$packedBrightness(long packedBrightness);

    long rple$packedBrightness();

    @SuppressWarnings("CastToIncompatibleInterface")
    static RPLETessellator rple$wrapTessellator(Tessellator tess) {
        return (RPLETessellator) tess;
    }
}
