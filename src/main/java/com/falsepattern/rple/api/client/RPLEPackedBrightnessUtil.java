/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api.client;

import com.falsepattern.lumina.api.lighting.LightType;
import com.falsepattern.rple.api.common.color.ColorChannel;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;

@SuppressWarnings("unused")
public final class RPLEPackedBrightnessUtil {
    private static final int BLOCK_BRIGHTNESS_BIT_LENGTH = Byte.SIZE;
    private static final int SKY_BRIGHTNESS_BIT_LENGTH = Byte.SIZE;
    private static final int BRIGHTNESS_CHANNEL_BIT_LENGTH = BLOCK_BRIGHTNESS_BIT_LENGTH +
                                                             SKY_BRIGHTNESS_BIT_LENGTH;

    private static final int BLOCK_BRIGHTNESS_BIT_SHIFT = 0;
    private static final int SKY_BRIGHTNESS_BIT_SHIFT = BLOCK_BRIGHTNESS_BIT_SHIFT +
                                                        BLOCK_BRIGHTNESS_BIT_LENGTH;
    private static final int BRIGHTNESS_CHANNEL_BIT_SHIFT = SKY_BRIGHTNESS_BIT_SHIFT;

    private static final long BLOCK_BRIGHTNESS_BIT_MASK = (1L << BLOCK_BRIGHTNESS_BIT_LENGTH) - 1L;
    private static final long SKY_BRIGHTNESS_BIT_MASK = (1L << SKY_BRIGHTNESS_BIT_LENGTH) - 1L;
    private static final long BRIGHTNESS_CHANNEL_BIT_MASK = (1L << BRIGHTNESS_CHANNEL_BIT_LENGTH) - 1L;

    private static final int BLUE_BRIGHTNESS_SEGMENT_BIT_LENGTH = BRIGHTNESS_CHANNEL_BIT_LENGTH;
    private static final int GREEN_BRIGHTNESS_SEGMENT_BIT_LENGTH = BRIGHTNESS_CHANNEL_BIT_LENGTH;
    private static final int RED_BRIGHTNESS_SEGMENT_BIT_LENGTH = BRIGHTNESS_CHANNEL_BIT_LENGTH;
    private static final int PACKED_BRIGHTNESS_BIT_LENGTH = BLUE_BRIGHTNESS_SEGMENT_BIT_LENGTH +
                                                            GREEN_BRIGHTNESS_SEGMENT_BIT_LENGTH +
                                                            RED_BRIGHTNESS_SEGMENT_BIT_LENGTH;

    private static final int BLUE_BRIGHTNESS_SEGMENT_BIT_SHIFT = 0;
    private static final int GREEN_BRIGHTNESS_SEGMENT_BIT_SHIFT = BLUE_BRIGHTNESS_SEGMENT_BIT_SHIFT +
                                                                  BLUE_BRIGHTNESS_SEGMENT_BIT_LENGTH;
    private static final int RED_BRIGHTNESS_SEGMENT_BIT_SHIFT = GREEN_BRIGHTNESS_SEGMENT_BIT_SHIFT +
                                                                GREEN_BRIGHTNESS_SEGMENT_BIT_LENGTH;

    private static final int PACKED_BRIGHTNESS_BIT_SHIFT = BLUE_BRIGHTNESS_SEGMENT_BIT_SHIFT;

    private static final long BLUE_SEGMENT_BIT_MASK = (1L << BLUE_BRIGHTNESS_SEGMENT_BIT_LENGTH) - 1L;
    private static final long GREEN_SEGMENT_BIT_MASK = (1L << GREEN_BRIGHTNESS_SEGMENT_BIT_LENGTH) - 1L;
    private static final long RED_SEGMENT_BIT_MASK = (1L << RED_BRIGHTNESS_SEGMENT_BIT_LENGTH) - 1L;
    private static final long PACKED_BRIGHTNESS_BIT_MASK = (1L << PACKED_BRIGHTNESS_BIT_SHIFT) - 1L;

    private RPLEPackedBrightnessUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static long packedBrightnessFromRGBLightValues(@NotNull LightType lightType,
                                                          int redLightValue,
                                                          int greenLightValue,
                                                          int blueLightValue) {
        return brightnessSegmentFromLightValue(RED_CHANNEL, lightType, redLightValue) |
               brightnessSegmentFromLightValue(GREEN_CHANNEL, lightType, greenLightValue) |
               brightnessSegmentFromLightValue(BLUE_CHANNEL, lightType, blueLightValue);
    }

    public static long packedBrightnessFromRGBLightValues(int redBlockLightValue,
                                                          int greenBlockLightValue,
                                                          int blueBlockLightValue,
                                                          int redSkyLightValue,
                                                          int greenSkyLightValue,
                                                          int blueSkyLightValue) {
        return brightnessSegmentFromLightValues(RED_CHANNEL, redBlockLightValue, redSkyLightValue) |
               brightnessSegmentFromLightValues(GREEN_CHANNEL, greenBlockLightValue, greenSkyLightValue) |
               brightnessSegmentFromLightValues(BLUE_CHANNEL, blueBlockLightValue, blueSkyLightValue);
    }

    public static long packedBrightnessFromLightValue(@NotNull LightType lightType, int lightValue) {
        return brightnessSegmentFromLightValue(RED_CHANNEL, lightType, lightValue) |
               brightnessSegmentFromLightValue(GREEN_CHANNEL, lightType, lightValue) |
               brightnessSegmentFromLightValue(BLUE_CHANNEL, lightType, lightValue);
    }

    public static long packedBrightnessFromLightValues(int blockLightValue, int skyLightValue) {
        return brightnessSegmentFromLightValues(RED_CHANNEL, blockLightValue, skyLightValue) |
               brightnessSegmentFromLightValues(GREEN_CHANNEL, blockLightValue, skyLightValue) |
               brightnessSegmentFromLightValues(BLUE_CHANNEL, blockLightValue, skyLightValue);
    }

    public static int lightValueFromPackedBrightness(long packedBrightness,
                                                     @NotNull ColorChannel channel,
                                                     @NotNull LightType lightType) {
        return 0;
    }

    public static int minLightValueFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static int minLightValueFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static int minLightValueFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static int maxLightValueFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static int maxLightValueFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static int maxLightValueFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static int avgLightValueFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static int avgLightValueFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static int avgLightValueFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static long packedBrightnessFromBrightnessChannel(@NotNull ColorChannel channel, long brightnessChannel) {
        return 0;
    }

    public static long packedBrightnessFromBrightnessChannels(long redLightChannel,
                                                              long greenLightChannel,
                                                              long blueLightChannel) {
        return 0;
    }

    public static long brightnessChannelFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static long minBrightnessChannelFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static long minBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static long minBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static long maxBrightnessChannelFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static long maxBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static long maxBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static long avgBrightnessChannelFromPackedBrightness(long packedBrightness) {
        return 0;
    }

    public static long avgBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull ColorChannel channel) {
        return 0;
    }

    public static long avgBrightnessChannelFromPackedBrightness(long packedBrightness, @NotNull LightType lightType) {
        return 0;
    }

    public static long minPackedBrightness(long packedBrightnessA, long packedBrightnessB) {
        return 0;
    }

    public static long maxPackedBrightness(long packedBrightnessA, long packedBrightnessB) {
        return 0;
    }

    public static long avgPackedBrightness(long packedBrightnessA, long packedBrightnessB) {
        return 0;
    }

    public static long mixAOPackedBrightness(long packedBrightnessA,
                                             long packedBrightnessB,
                                             double multA,
                                             double multB) {
        return 0;
    }

    public static long mixAOPackedBrightness(long packedBrightnessAC,
                                             long packedBrightnessBC,
                                             long packedBrightnessBD,
                                             long packedBrightnessAD,
                                             double alphaAB,
                                             double alphaCD) {
        val multAC = (1D - alphaAB) * (1D - alphaCD);
        val multBC = (1D - alphaAB) * alphaCD;
        val multBD = alphaAB * (1D - alphaCD);
        val multAD = alphaAB * alphaCD;
        return mixAOPackedBrightness(packedBrightnessAC,
                                     packedBrightnessBC,
                                     packedBrightnessBD,
                                     packedBrightnessAD,
                                     multAC,
                                     multBC,
                                     multBD,
                                     multAD);
    }

    public static long mixAOPackedBrightness(long packedBrightnessA,
                                             long packedBrightnessB,
                                             long packedBrightnessC,
                                             long packedBrightnessD,
                                             double multA,
                                             double multB,
                                             double multC,
                                             double multD) {
        return 0;
    }

    private static long brightnessSegmentFromLightValue(ColorChannel channel, LightType lightType, int lightValue) {
        val brightnessChannel = brightnessChannelFromLightValue(lightType, lightValue);
        switch (channel) {
            default:
            case RED_CHANNEL:
                return brightnessChannel << RED_BRIGHTNESS_SEGMENT_BIT_SHIFT;
            case GREEN_CHANNEL:
                return brightnessChannel << GREEN_BRIGHTNESS_SEGMENT_BIT_SHIFT;
            case BLUE_CHANNEL:
                return brightnessChannel << BLUE_BRIGHTNESS_SEGMENT_BIT_SHIFT;
        }
    }

    private static long brightnessSegmentFromLightValues(ColorChannel channel, int blockLightValue, int skyLightValue) {
        val brightnessChannel = brightnessChannelFromLightValues(blockLightValue, skyLightValue);
        switch (channel) {
            default:
            case RED_CHANNEL:
                return brightnessChannel << RED_BRIGHTNESS_SEGMENT_BIT_SHIFT;
            case GREEN_CHANNEL:
                return brightnessChannel << GREEN_BRIGHTNESS_SEGMENT_BIT_SHIFT;
            case BLUE_CHANNEL:
                return brightnessChannel << BLUE_BRIGHTNESS_SEGMENT_BIT_SHIFT;
        }
    }

    private static long brightnessChannelFromLightValue(LightType lightType, int lightValue) {
        switch (lightType) {
            default:
            case BLOCK_LIGHT_TYPE:
                return (lightValue | BLOCK_BRIGHTNESS_BIT_MASK) << BLOCK_BRIGHTNESS_BIT_SHIFT;
            case SKY_LIGHT_TYPE:
                return (lightValue | SKY_BRIGHTNESS_BIT_MASK) << SKY_BRIGHTNESS_BIT_SHIFT;
        }
    }

    private static long brightnessChannelFromLightValues(int blockLightValue, int skyLightValue) {
        return ((blockLightValue | BLOCK_BRIGHTNESS_BIT_MASK) << BLOCK_BRIGHTNESS_BIT_SHIFT) |
               ((skyLightValue | SKY_BRIGHTNESS_BIT_MASK) << SKY_BRIGHTNESS_BIT_SHIFT);
    }
}
