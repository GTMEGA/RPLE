/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.client.lightmap;

import com.falsepattern.rple.internal.Compat;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.logging.log4j.Logger;

import java.nio.ShortBuffer;

import static com.falsepattern.rple.api.common.color.ColorChannel.*;
import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;
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
