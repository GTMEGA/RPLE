package com.falsepattern.rple.internal;

public class Utils {
    public static final int MASK = 0x40000000;
    public static final int MIN;
    static {
        int res = MASK;
        for (int i = 0; i <= 25; i++) {
            res |= 1 << i;
        }
        MIN = res;
    }

    public static int packedMax(int packedA, int packedB) {
        int result = MASK;
        for (int i = 0; i <= 25; i += 5) {
            int a = (packedA >>> i) & 0x1F;
            int b = (packedB >>> i) & 0x1F;
            result |= (a < b) ? b << i : a << i;
        }
        return result;
    }

    public static float extractFloat(int packed, int offset) {
        return (((packed >>> (offset + 1)) & 0xF) << 4);
    }
}
