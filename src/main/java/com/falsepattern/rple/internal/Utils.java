package com.falsepattern.rple.internal;

import com.falsepattern.rple.api.ColoredBlock;
import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import lombok.val;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Utils {
    // Cookie format (bits):
    // 0100 0000 IIII IIII IIII IIII 0000 000P
    public static final int INDEX_SHIFT = 8;
    public static final int PARITY_BIT = 0x1;
    public static final int INDEX_MASK = 0xFFFF << INDEX_SHIFT;
    public static final int COOKIE_BIT = 0x40000000;
    public static final int ZERO_MASK  = ~(PARITY_BIT | INDEX_MASK | COOKIE_BIT);

    public static final int TYPE_COOKIE = 0;
    public static final int TYPE_VANILLA = 1;
    public static final int TYPE_BROKEN = 2;
    public static final long BROKEN_WARN_COLOR;

    public static final int PACKED_BIT_BLOCK_COLOR = 0x20000000;
    public static final int MIN;

    //Long format (hex):
    // 0000 RRrr GGgg BBbb
    public static final int RED_PAIR_OFFSET = 32;
    public static final int GREEN_PAIR_OFFSET = 16;
    public static final int BLUE_PAIR_OFFSET = 0;

    private static boolean warnedBefore = false;
    public static final String[] IDs = new String[]{"RED", "GREEN", "BLUE"};

    private static final long[] lightValues = new long[65536];
    private static int counter = 1;
    static {
        long res = 0;
        for (int i = 4; i <= 40; i += 8) {
            res |= 0xFL << i;
        }
        lightValues[0] = res;
        MIN = packedLongToCookie(res);
        BROKEN_WARN_COLOR = lightsToPackedLong(createPair(0xF, 0xF), 0, 0);
    }

    public static int packedLongToCookie(long packedLong) {
        int cookie = counter++;
        if (counter >= lightValues.length) {
            counter = 1;
        }
        lightValues[cookie] = packedLong;
        cookie = (cookie << INDEX_SHIFT) | COOKIE_BIT;
        return cookie | parity(cookie);
    }

    public static int inspectValue(int potentialCookie) {
        if ((potentialCookie & COOKIE_BIT) != 0 && parity(potentialCookie) == 0 && (potentialCookie & ZERO_MASK) == 0) {
            return TYPE_COOKIE;
        } else if ((potentialCookie & 0x00FF00FF) == potentialCookie) {
            return TYPE_VANILLA;
        } else {
            return TYPE_BROKEN;
        }
    }

    private static int parity(int x) {
        int y = x ^ (x >>> 1);
        y = y ^ (y >>> 2);
        y = y ^ (y >>> 4);
        y = y ^ (y >>> 8);
        y = y ^ (y >>> 16);

        return y & 1;
    }

    public static long cookieToPackedLong(int cookie) {
        switch (inspectValue(cookie)) {
            case TYPE_COOKIE: {
                return lightValues[(cookie & INDEX_MASK) >>> INDEX_SHIFT];
            }
            case TYPE_VANILLA: {
                //Vanilla fake-pack
                val packed = packPair(cookie);
                return ((long) packed << RED_PAIR_OFFSET) | ((long) packed << GREEN_PAIR_OFFSET) | (packed << BLUE_PAIR_OFFSET);
            }
            default: {
                if (!warnedBefore) {
                    Common.LOG.error(new IllegalArgumentException("Illegal brightness value (did it get corrupted?) " + Integer.toHexString(cookie)));
                    warnedBefore = true;
                }
                return BROKEN_WARN_COLOR;
            }
        }
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

    public static int getLightBrightnessForSkyBlocksAccess(IBlockAccess access, int x, int y, int z, int minBlockLight) {
        int minRed, minGreen, minBlue;
        if ((minBlockLight & PACKED_BIT_BLOCK_COLOR) != 0) {
            minRed = (minBlockLight >> 8) & 0xF;
            minGreen = (minBlockLight >> 4) & 0xF;
            minBlue = minBlockLight & 0xF;
        } else {
            minRed = minGreen = minBlue = minBlockLight;
        }
        val carrier = access instanceof World ? ((ColoredCarrierWorld) access) : (ColoredCarrierWorld)(((ChunkCache)access).worldObj);
        val red = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_RED).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minRed);
        val green = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_GREEN).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minGreen);
        val blue = carrier.getColoredWorld(LightConstants.COLOR_CHANNEL_BLUE).getLightBrightnessForSkyBlocksWorld(access, x, y, z, minBlue);
        return packedLongToCookie(lightsToPackedLong(red, green, blue));
    }

    public static int getLightValuePacked(IBlockAccess world, ColoredBlock block, int meta, int x, int y, int z) {
        return Utils.PACKED_BIT_BLOCK_COLOR |
               block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_RED, x, y, z) << 8 |
               block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_GREEN, x, y, z) << 4 |
               block.getColoredLightValue(world, meta, LightConstants.COLOR_CHANNEL_BLUE, x, y, z);
    }


}
