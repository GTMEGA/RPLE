package com.falsepattern.rple.internal;

public class Utils {
    public static final int COOKIE_BIT = 0x40000000;
    public static final int PACKED_BIT_BLOCK_COLOR = 0x20000000;
    public static final int MIN;
    public static final int RED_PAIR_OFFSET = 32;
    public static final int GREEN_PAIR_OFFSET = 16;
    public static final int BLUE_PAIR_OFFSET = 0;
    private static long[] lightValues = new long[16384];
    private static int counter = 1;
    static {
        long res = 0;
        for (int i = 4; i <= 40; i += 8) {
            res |= 0xFL << i;
        }
        lightValues[0] = res;
        MIN = COOKIE_BIT;
    }

    public static int createPair(int block, int sky) {
        return (sky & 0xF) << 20 | (block & 0xF) << 4;
    }

    private static int packPair(int pair) {
        return ((pair & 0x00FF0000) >>> 8) | (pair & 0xFF);
    }

    private static int unpackPair(int pair) {
        return ((pair & 0x0000FF00) << 8) | (pair & 0xFF);
    }

    public static long lightsToPackedLong(int redPair, int greenPair, int bluePair) {
        return (long) packPair(redPair) << RED_PAIR_OFFSET |
               (long) packPair(greenPair) << GREEN_PAIR_OFFSET |
               (long) packPair(bluePair) << BLUE_PAIR_OFFSET;
    }

    public static int getRedPair(long packed) {
        return unpackPair((int) ((packed >>> RED_PAIR_OFFSET) & 0xFFFFL));
    }

    public static int getGreenPair(long packed) {
        return unpackPair((int)((packed >>> GREEN_PAIR_OFFSET) & 0xFFFFL));
    }

    public static int getBluePair(long packed) {
        return unpackPair((int)((packed >>> BLUE_PAIR_OFFSET) & 0xFFFFL));
    }

    public static synchronized int packedLongToCookie(long packedLong) {
        int cookie = counter++;
        if (counter >= lightValues.length) {
            counter = 1;
        }
        lightValues[cookie] = packedLong;
        return cookie | COOKIE_BIT;
    }

    public static long cookieToPackedLong(int cookie) {
        if ((cookie & COOKIE_BIT) == 0) {
            new IllegalArgumentException(Integer.toHexString(cookie) + " is not a cookie!").printStackTrace();
            return 0;
        }
        return lightValues[cookie ^ COOKIE_BIT];
    }

    public static int packedMax(int cookieA, int cookieB) {
        return Utils.packedLongToCookie(packedMax(Utils.cookieToPackedLong(cookieA), Utils.cookieToPackedLong(cookieB)));
    }

    private static long packedMax(long packedA, long packedB) {
        long result = 0L;
        for (int i = 0; i <= 40; i += 8) {
            long a = (packedA >>> i) & 0xFF;
            long b = (packedB >>> i) & 0xFF;
            result |= (a < b) ? b << i : a << i;
        }
        return result;
    }

    public static float extractFloat(int packed, int offset) {
        return (((packed >>> (offset + 1)) & 0xF) << 4);
    }
}
