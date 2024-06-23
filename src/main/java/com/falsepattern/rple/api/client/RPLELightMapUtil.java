package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.client.lightmap.LightMap;

@Deprecated
public final class RPLELightMapUtil {
    // TODO: This is the only method from the API used by Neodymium, can we give it special access to it somehow?
    public static void enableVertexPointersVBO() {
        LightMap.lightMap().enableVertexPointersVBO();
    }
}
