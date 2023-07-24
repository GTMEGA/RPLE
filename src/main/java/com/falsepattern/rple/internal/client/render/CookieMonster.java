/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.client.render;

import com.falsepattern.rple.internal.common.collection.CircularLongBuffer;
import com.falsepattern.rple.internal.mixin.mixins.client.TessellatorMixin;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.falsepattern.rple.internal.RightProperLightingEngine.createLogger;

/**
 * All parts of Minecraft's rendering internals expect light levels an int, but we can only fit our data into longs.
 * Therefore, this class is used for "converting" our longs into ints, using an internal circular queue with a size
 * large enough to avoid any collisions, and the ints being indices into this array.
 * <p>
 * Injected code returns an int, which passes through MC code, and then we convert them back to longs inside the
 * {@link TessellatorMixin}.
 * <p>
 * Note: Cookies should be treated as opaque blobs. Do not try to unpack them manually, always go through the cookie
 * manager! Additionally, they should not be saved anywhere, as they expire very quickly.
 */
public final class CookieMonster {
    private static final Logger LOG = createLogger("CookieMonster");

    // Cookie format (bits):
    // 0100 0000 IIII IIII IIII IIII 0000 000P
    // I - index bits
    // P - parity
    private static final int NUM_INDICES = 0x10000;
    private static final int INDEX_SHIFT = 8;
    private static final int PARITY_BIT = 0x00000001;
    private static final int INDEX_MASK = (NUM_INDICES - 1) << INDEX_SHIFT;
    private static final int COOKIE_BIT = 0x40000000;
    private static final int ZERO_MASK = ~(PARITY_BIT | INDEX_MASK | COOKIE_BIT);

    private static final int BRIGHTNESS_MASK = 0x00FF00FF;

    private static final long BROKEN_WARN_COLOR;

    static {
        val redBrightness = TessellatorBrightnessHelper
                .lightLevelsToBrightnessForTessellator(0xF, 0xF);
        val greenBrightness = TessellatorBrightnessHelper
                .lightLevelsToBrightnessForTessellator(0x0, 0x0);
        val blueBrightness = TessellatorBrightnessHelper
                .lightLevelsToBrightnessForTessellator(0x0, 0x0);
        BROKEN_WARN_COLOR = TessellatorBrightnessHelper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                                          greenBrightness,
                                                                                                          blueBrightness);
    }

    private static final CircularLongBuffer LIGHT_VALUES = new CircularLongBuffer(NUM_INDICES);
    private static final AtomicBoolean WARNED_BEFORE = new AtomicBoolean(false);

    /**
     * @param packedLong A long value returned by {@link TessellatorBrightnessHelper}.
     * @return An opaque, temporary cookie representing the given long.
     */
    public static int packedLongToCookie(long packedLong) {
        val index = LIGHT_VALUES.put(packedLong);
        val cookie = ((index << INDEX_SHIFT) & INDEX_MASK) | COOKIE_BIT;
        return cookie | parity(cookie);
    }

    /**
     * @param cookie A cookie returned by {@link CookieMonster#packedLongToCookie(long)}, or a vanilla minecraft brightness.
     * @return The long value represented by the cookie, or the vanilla minecraft brightness turned into a greyscale
     * packed long. If it's neither, then we assume that it's a corrupted cookie, and return a bright red warning color,
     * and log an error (first time only).
     */
    public static long cookieToPackedLong(int cookie) {
        switch (inspectValue(cookie)) {
            case COOKIE: {
                return LIGHT_VALUES.get((cookie & INDEX_MASK) >>> INDEX_SHIFT);
            }
            case VANILLA: {
                // Vanilla fake-pack
                return TessellatorBrightnessHelper.packedBrightnessFromTessellatorBrightnessChannels(cookie, cookie, cookie);
            }
            default: {
                if (!WARNED_BEFORE.get()) {
                    WARNED_BEFORE.set(true);
                    //  Not throwing an exception here, this is only a graphical bug.
                    //  Graphical bugs shouldn't cause crashes.
                    LOG.error(new IllegalArgumentException("Illegal brightness value (did it get corrupted?) " +
                                                           Integer.toHexString(cookie)));
                }
                return BROKEN_WARN_COLOR;
            }
        }
    }

    /**
     * Analyzes a potential cookie, and returns the detected type.
     *
     * @param potentialCookie The cookie to analyze.
     * @return {@link IntType#COOKIE} if it was a correct cookie, {@link IntType#VANILLA} if it was a vanilla minecraft
     * brightness value, and {@link IntType#BROKEN} if it was neither.
     */
    public static IntType inspectValue(int potentialCookie) {
        if ((potentialCookie & COOKIE_BIT) != 0 && parity(potentialCookie) == 0 && (potentialCookie & ZERO_MASK) == 0) {
            return IntType.COOKIE;
        } else if ((potentialCookie & BRIGHTNESS_MASK) == potentialCookie) {
            return IntType.VANILLA;
        } else {
            return IntType.BROKEN;
        }
    }

    /**
     * O(1) parity function taken from Hacker's Delight (ISBN 978-0-321-84268-8).
     *
     * @return 0 if the number has even bit-parity, 1 if the number has odd bit-parity.
     */
    private static int parity(int x) {
        var y = x ^ (x >>> 1);
        y ^= y >>> 2;
        y ^= y >>> 4;
        y ^= y >>> 8;
        y ^= y >>> 16;

        return y & 1;
    }

    /**
     * See {@link CookieMonster#inspectValue(int)}.
     */
    public enum IntType {
        COOKIE,
        VANILLA,
        BROKEN
    }
}
