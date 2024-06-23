package com.falsepattern.rple.api.client.render;

import net.minecraft.client.renderer.Tessellator;

public interface RPLETessellator {
    @SuppressWarnings("CastToIncompatibleInterface")
    static RPLETessellator of(Tessellator tess) {
        return (RPLETessellator) tess;
    }

    void rple$packedBrightness(long packedBrightness);

    long rple$packedBrightness();
}
