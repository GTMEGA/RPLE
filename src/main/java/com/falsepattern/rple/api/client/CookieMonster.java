/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.common.collection.CircularLongBuffer;
import com.falsepattern.rple.internal.common.util.FastThreadLocal;
import com.falsepattern.rple.internal.mixin.mixins.client.TessellatorMixin;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static com.falsepattern.rple.internal.common.config.RPLEConfig.Debug.DEBUG_COOKIE_MONSTER;
import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
import static com.falsepattern.rple.internal.common.util.LogHelper.shouldLogDebug;

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
// TODO: [COOKIE] Pull this up into API
public final class CookieMonster {
    private static final Logger LOG = createLogger("CookieMonster");

    // Cookie format (bits):
    // Packed long
    // 0100 0000 IIII IIII IIII IIII CCCC CCCP
    // I - index bits
    // C - check bits
    // P - parity
    //
    // Cookied 4 bit RGB
    // 0110 0000 BBBB GGGG RRRR bbbb gggg rrrr
    private static final int NUM_INDICES = 0x100000;
    private static final int INDEX_SHIFT = 8;
    private static final int PARITY_BIT = 0x0000_0001;
    private static final int INDEX_MASK = (NUM_INDICES - 1) << INDEX_SHIFT;
    private static final int CHECK_SHIFT = 1;
    private static final int NUM_CHECKS = 0b1_0000_000;
    private static final int CHECK_MASK = (NUM_CHECKS - 1) << CHECK_SHIFT;
    private static final int COOKIE_BIT = 0x4000_0000;
    private static final int ZERO_MASK_COOKIE = ~(PARITY_BIT | INDEX_MASK | CHECK_MASK | COOKIE_BIT);

    private static final int RGB_BIT = 0x2000_0000;
    private static final int RGB_MASK = 0x00FF_FFFF;
    private static final int ZERO_MASK_RGB = ~(COOKIE_BIT | RGB_BIT | RGB_MASK);

    private static final int BRIGHTNESS_MASK = 0x00FF_00FF;

    private static final long BROKEN_WARN_COLOR;

    private static final FastThreadLocal.FixedValue<ThreadState> THREAD_STATE = new FastThreadLocal.FixedValue<>(ThreadState::new);

    static {
        val redBrightness = RGB64Helper
                .lightLevelsToBrightnessForTessellator(0xF, 0xF);
        val greenBrightness = RGB64Helper
                .lightLevelsToBrightnessForTessellator(0x0, 0x0);
        val blueBrightness = RGB64Helper
                .lightLevelsToBrightnessForTessellator(0x0, 0x0);
        BROKEN_WARN_COLOR = RGB64Helper.packedBrightnessFromTessellatorBrightnessChannels(redBrightness,
                                                                                          greenBrightness,
                                                                                          blueBrightness);
    }

    /**
     * See {@link CookieMonster#inspectValue(int)}.
     */
    public enum IntType {
        COOKIE,
        VANILLA,
        BROKEN
    }

    /**
     * @param packedLong A long value returned by {@link RGB64Helper}.
     * @return An opaque, temporary cookie representing the given long.
     */
    public static int packedLongToCookie(long packedLong) {
        int rgb = RGB64Helper.tryRGBFromPackedBrightness(packedLong);
        if (rgb != -1) {
            return rgb | COOKIE_BIT | RGB_BIT;
        }
        val state = THREAD_STATE.get();
        val index = state.lightValues.put(packedLong);
        val cookie = ((index << INDEX_SHIFT) & INDEX_MASK) | (state.check << CHECK_SHIFT) | COOKIE_BIT;
        return cookie | parity(cookie);
    }

    public static int rgbToCookie(int rgb) {
        return (rgb & RGB_MASK) | COOKIE_BIT | RGB_BIT;
    }

    /**
     * @param cookie A cookie returned by {@link CookieMonster#packedLongToCookie(long)}, or a vanilla minecraft brightness.
     * @return The long value represented by the cookie, or the vanilla minecraft brightness turned into a greyscale
     * packed long. If it's neither, then we assume that it's a corrupted cookie, and return a bright red warning color,
     * and log an error (first time only).
     */
    public static long cookieToPackedLong(int cookie) {
        switch (inspectValue(cookie)) {
            case COOKIE:
                if ((cookie & RGB_BIT) != 0) {
                    return RGB64Helper.packedBrightnessFromRGB(cookie & RGB_MASK);
                }
                return THREAD_STATE.get().lightValues.get((cookie & INDEX_MASK) >>> INDEX_SHIFT);
            case VANILLA:
                // Vanilla fake-pack
                return RGB64Helper.packedBrightnessFromTessellatorBrightnessChannels(cookie, cookie, cookie);
            default:
                if (shouldLogDebug(DEBUG_COOKIE_MONSTER)) {
                    LOG.warn("Illegal brightness value (Did a mod treat a cookie as a regular brightness value?)");
                    LOG.trace("Stack trace:", new IllegalStateException());
                }
                return BROKEN_WARN_COLOR;
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
        if ((potentialCookie & COOKIE_BIT) == 0) {
            if ((potentialCookie & BRIGHTNESS_MASK) == potentialCookie)
                return IntType.VANILLA;
            return IntType.BROKEN;
        }
        if ((potentialCookie & RGB_BIT) != 0 && (potentialCookie & ZERO_MASK_RGB) == 0) {
            return IntType.COOKIE;
        }
        if (parity(potentialCookie) == 0 && (potentialCookie & ZERO_MASK_COOKIE) == 0) {
            val state = THREAD_STATE.get();
            if (((potentialCookie & CHECK_MASK) >>> CHECK_SHIFT) != state.check) {
               if (shouldLogDebug(DEBUG_COOKIE_MONSTER)) {
                   LOG.warn("Cookie passed through thread boundary{}", Compat.falseTweaksThreadedChunksEnabled() ? " (Is a mod not compatible with FalseTweaks Threaded Chunks?)" : "");
                   LOG.trace("Stack trace:", new IllegalStateException());
               }
            }
            return IntType.COOKIE;
        }
        return IntType.BROKEN;
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

    private static class ThreadState {
        private static final AtomicInteger checkIndexCounter = new AtomicInteger(0);

        private final int check = checkIndexCounter.getAndUpdate(x -> (x + 1) % NUM_CHECKS);
        private final CircularLongBuffer lightValues = new CircularLongBuffer(NUM_INDICES);
    }
}
