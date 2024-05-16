package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.internal.Compat;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Logger;

import java.nio.ShortBuffer;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.LogHelper.createLogger;
import static com.falsepattern.rple.internal.client.lightmap.LightMapPipeline.lightMapPipeline;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class LightMap {
    private static final Logger LOG = createLogger("LightMap");

    private static final LightMap INSTANCE = new LightMap();

    private LightMapTexture rTexture;
    private LightMapTexture gTexture;
    private LightMapTexture bTexture;

    private boolean texturesGenerated = false;

    public static LightMap lightMap() {
        return INSTANCE;
    }

    public void generateTextures() {
        if (texturesGenerated)
            return;

        rTexture = LightMapTexture.createLightMapTexture(RED_CHANNEL);
        gTexture = LightMapTexture.createLightMapTexture(GREEN_CHANNEL);
        bTexture = LightMapTexture.createLightMapTexture(BLUE_CHANNEL);

        texturesGenerated = true;
        LOG.info("Created LightMap");
    }

    public void update(float partialTick) {
        val pixels = lightMapPipeline().update(partialTick);
        rTexture.update(pixels);
        gTexture.update(pixels);
        bTexture.update(pixels);
    }

    public void toggleEnabled(boolean enabled) {
        if (Compat.shadersEnabled())
            Compat.toggleLightMapShaders(enabled);

        rTexture.toggleEnabled(enabled);
        gTexture.toggleEnabled(enabled);
        bTexture.toggleEnabled(enabled);
    }

    public void prepare() {
        rescale();
        bind();
    }

    public void bind() {
        rTexture.bind();
        gTexture.bind();
        bTexture.bind();
    }

    public void rescale() {
        rTexture.rescale();
        gTexture.rescale();
        bTexture.rescale();
    }

    public void enableVertexPointers(ShortBuffer buffer) {
        rTexture.enableVertexPointer(buffer);
        gTexture.enableVertexPointer(buffer);
        bTexture.enableVertexPointer(buffer);
    }

    public void enableVertexPointersVBO() {
        rTexture.enableVertexPointerVBO();
        gTexture.enableVertexPointerVBO();
        bTexture.enableVertexPointerVBO();
    }

    public void disableVertexPointers() {
        rTexture.disableVertexPointer();
        gTexture.disableVertexPointer();
        bTexture.disableVertexPointer();
    }
}
