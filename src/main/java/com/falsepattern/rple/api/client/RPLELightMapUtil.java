package com.falsepattern.rple.api.client;

import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Common;
import org.lwjgl.opengl.GL13;

@SuppressWarnings("unused")
public final class RPLELightMapUtil {
    private static final float LIGHT_MAP_COORDINATE_SCALE = Short.MAX_VALUE;
    private static final float LIGHT_MAP_BASE_COORDINATE_SCALE = 255F / Short.MAX_VALUE;
    private static final float LIGHT_MAP_TEXTURE_SCALE = 1F / LIGHT_MAP_COORDINATE_SCALE;
    private static final float LIGHT_MAP_TEXTURE_TRANSLATION = 1024F;

    private RPLELightMapUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static float lightMapBaseCoordinateScale() {
        return LIGHT_MAP_COORDINATE_SCALE;
    }

    public static float lightMapCoordinateScale() {
        return LIGHT_MAP_BASE_COORDINATE_SCALE;
    }

    public static float lightMapTextureScale() {
        return LIGHT_MAP_TEXTURE_SCALE;
    }

    public static float lightMapTextureTranslation() {
        return LIGHT_MAP_TEXTURE_TRANSLATION;
    }

    /**
     * @param cookie Cookie brightness reference
     */
    public static void configureLightMapWithCookie(int cookie) {

    }

    /**
     * @param packed Packed brightness value
     */
    public static void configureLightMapWithPacked(long packed) {

    }

    /**
     * @param type   Coordinate type
     * @param blockR Red block light
     * @param blockG Green block light
     * @param blockB Blue block light
     * @param skyR   Red sky light
     * @param skyG   Green sky light
     * @param skyB   Blue sky light
     */
    public static void configureLightMap(LightMapCoordinateType type,
                                         float blockR,
                                         float blockG,
                                         float blockB,
                                         float skyR,
                                         float skyG,
                                         float skyB) {
        switch (type) {
            case BASE:
                blockR *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                blockG *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                blockB *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                skyR *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                skyG *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                skyB *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                break;
            case NORMALIZED:
                blockR *= LIGHT_MAP_COORDINATE_SCALE;
                blockG *= LIGHT_MAP_COORDINATE_SCALE;
                blockB *= LIGHT_MAP_COORDINATE_SCALE;
                skyR *= LIGHT_MAP_COORDINATE_SCALE;
                skyG *= LIGHT_MAP_COORDINATE_SCALE;
                skyB *= LIGHT_MAP_COORDINATE_SCALE;
                break;
            default:
            case SCALED:
                break;
        }

        GL13.glMultiTexCoord2f(Common.RED_LIGHT_MAP_TEXTURE_UNIT, blockR, skyR);
        GL13.glMultiTexCoord2f(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, blockG, skyG);
        GL13.glMultiTexCoord2f(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, blockB, skyB);
    }

    /**
     * @param channel Color channel
     * @param type    Coordinate type
     * @param block   Block light
     * @param sky     Sky light
     */
    public static void configureLightMap(ColorChannel channel, LightMapCoordinateType type, float block, float sky) {
        switch (type) {
            case BASE:
                block *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                break;
            case NORMALIZED:
                block *= LIGHT_MAP_COORDINATE_SCALE;
                break;
            default:
            case SCALED:
                break;
        }

        switch (channel) {
            default:
            case RED_CHANNEL:
                GL13.glMultiTexCoord2f(Common.RED_LIGHT_MAP_TEXTURE_UNIT, block, sky);
                break;
            case GREEN_CHANNEL:
                GL13.glMultiTexCoord2f(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, block, sky);
                break;
            case BLUE_CHANNEL:
                GL13.glMultiTexCoord2f(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, block, sky);
                break;
        }
    }

    /**
     * @param type  Coordinate type
     * @param block Block light
     * @param sky   Sky light
     */
    public static void configureLightMap(LightMapCoordinateType type, float block, float sky) {
        switch (type) {
            case BASE:
                block *= LIGHT_MAP_BASE_COORDINATE_SCALE;
                break;
            case NORMALIZED:
                block *= LIGHT_MAP_COORDINATE_SCALE;
                break;
            default:
            case SCALED:
                break;
        }

        GL13.glMultiTexCoord2f(Common.RED_LIGHT_MAP_TEXTURE_UNIT, block, sky);
        GL13.glMultiTexCoord2f(Common.GREEN_LIGHT_MAP_TEXTURE_UNIT, block, sky);
        GL13.glMultiTexCoord2f(Common.BLUE_LIGHT_MAP_TEXTURE_UNIT, block, sky);
    }

    public enum LightMapCoordinateType {
        BASE, NORMALIZED, SCALED
    }
}
