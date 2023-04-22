package com.falsepattern.rple.internal;

public class Constants {
    public static final int BYTES_PER_VERTEX_VANILLA = 8;
    public static final int BYTES_PER_VERTEX_COLOR = BYTES_PER_VERTEX_VANILLA + 2 * 4;

    public static int extendBytesPerVertex(int vanilla) {
        return (vanilla / BYTES_PER_VERTEX_VANILLA) * BYTES_PER_VERTEX_COLOR;
    }
}
