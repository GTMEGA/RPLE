/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.Compat;
import com.falsepattern.rple.internal.client.render.VertexConstants;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.falsepattern.rple.internal.client.lightmap.LightMapConstants.*;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class LightMapTexture {
    private static IntBuffer PIXEL_BUFFER;
    private static int FIXED_VERTEX_STRIDE;
    private static int SHADER_VERTEX_STRIDE;

    private final int textureID;

    private final int colorBitMask;
    private final int fixedTextureUnitBinding;
    private final int shaderTextureSamplerBinding;
    private final int shaderTextureCoordsBinding;
    private final int fixedVertexPosition;
    private final int shaderVertexPosition;

    public static LightMapTexture createLightMapTexture(ColorChannel channel) {
        if (PIXEL_BUFFER == null) {
            PIXEL_BUFFER = ByteBuffer.allocateDirect(LIGHT_MAP_2D_SIZE * Integer.BYTES)
                                     .order(ByteOrder.nativeOrder())
                                     .asIntBuffer();
            FIXED_VERTEX_STRIDE = VertexAPI.recomputeVertexInfo(8, 4);
            SHADER_VERTEX_STRIDE = VertexAPI.recomputeVertexInfo(18, 4);
        }
        val textureID = GL11.glGenTextures();

        val lastTextureName = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTextureName);

        final int colorBitMask;
        final int fixedTextureUnitBinding;
        final int shaderTextureSamplerBinding;
        final int shaderTextureCoordsBinding;
        final int fixedVertexPosition;
        final int shaderVertexPosition;
        switch (channel) {
            default:
            case RED_CHANNEL:
                colorBitMask = R_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = R_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = R_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                shaderTextureCoordsBinding = R_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                fixedVertexPosition = VertexConstants.getRedIndexNoShader() * 2;
                shaderVertexPosition = VertexConstants.getRedIndexShader() * 2;
                break;
            case GREEN_CHANNEL:
                colorBitMask = G_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = G_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = G_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                shaderTextureCoordsBinding = G_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                fixedVertexPosition = VertexConstants.getGreenIndexNoShader() * 2;
                shaderVertexPosition = VertexConstants.getGreenIndexShader() * 2;
                break;
            case BLUE_CHANNEL:
                colorBitMask = B_LIGHT_MAP_COLOR_BIT_MASK;
                fixedTextureUnitBinding = B_LIGHT_MAP_FIXED_TEXTURE_UNIT_BINDING;
                shaderTextureSamplerBinding = B_LIGHT_MAP_SHADER_TEXTURE_SAMPLER_BINDING;
                shaderTextureCoordsBinding = B_LIGHT_MAP_SHADER_TEXTURE_COORDS_BINDING;
                fixedVertexPosition = VertexConstants.getBlueIndexNoShader() * 2;
                shaderVertexPosition = VertexConstants.getBlueIndexShader() * 2;
                break;
        }
        return new LightMapTexture(textureID,
                                   colorBitMask,
                                   fixedTextureUnitBinding,
                                   shaderTextureSamplerBinding,
                                   shaderTextureCoordsBinding,
                                   fixedVertexPosition,
                                   shaderVertexPosition);
    }

    public void update(int[] pixels) {
        PIXEL_BUFFER.clear();
        for (int i = 0; i < LIGHT_MAP_2D_SIZE; i++) {
            PIXEL_BUFFER.put(pixels[i] | colorBitMask);
        }
        PIXEL_BUFFER.flip();

        val lastTextureName = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                          0,
                          GL11.GL_RGBA,
                          16,
                          16,
                          0,
                          GL12.GL_BGRA,
                          GL11.GL_UNSIGNED_BYTE,
                          PIXEL_BUFFER);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, lastTextureName);
    }

    public void toggleEnabled(boolean enabled) {
        if (Compat.shadersEnabled())
            return;

        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        GL13.glActiveTexture(fixedTextureUnitBinding);
        if (enabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        GL13.glActiveTexture(lastActiveTexture);
    }

    public void bind() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureSamplerBinding);
        } else {
            GL13.glActiveTexture(fixedTextureUnitBinding);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL13.glActiveTexture(lastActiveTexture);
    }

    public void unbind() {
        if (!Compat.shadersEnabled()) {
            val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
            GL13.glActiveTexture(fixedTextureUnitBinding);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL13.glActiveTexture(lastActiveTexture);
        }
    }

    public void resetScale() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureCoordsBinding);
        } else {
            GL13.glActiveTexture(fixedTextureUnitBinding);
        }
        val lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(lastMatrixMode);
        GL13.glActiveTexture(lastActiveTexture);
    }

    public void rescale() {
        val lastActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glActiveTexture(shaderTextureCoordsBinding);
        } else {
            GL13.glActiveTexture(fixedTextureUnitBinding);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        val lastMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glScalef(LIGHT_MAP_TEXTURE_SCALE, LIGHT_MAP_TEXTURE_SCALE, 1F);
        GL11.glTranslatef(LIGHT_MAP_TEXTURE_TRANSLATION, LIGHT_MAP_TEXTURE_TRANSLATION, 0F);

        GL11.glMatrixMode(lastMatrixMode);
        GL13.glActiveTexture(lastActiveTexture);
    }

    public void enableVertexPointer(ShortBuffer buffer) {
        val lastClientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glClientActiveTexture(shaderTextureCoordsBinding);
            buffer.position(shaderVertexPosition);
            GL11.glTexCoordPointer(2, SHADER_VERTEX_STRIDE, buffer);
        } else {
            GL13.glClientActiveTexture(fixedTextureUnitBinding);
            buffer.position(fixedVertexPosition);
            GL11.glTexCoordPointer(2, FIXED_VERTEX_STRIDE, buffer);
        }

        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        GL13.glClientActiveTexture(lastClientActiveTexture);
    }

    public void enableVertexPointerVBO() {
        val lastClientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
        GL13.glClientActiveTexture(shaderTextureCoordsBinding);
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, SHADER_VERTEX_STRIDE, shaderVertexPosition * 2L);

        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        GL13.glClientActiveTexture(lastClientActiveTexture);
    }

    public void disableVertexPointer() {
        val lastClientActiveTexture = GL11.glGetInteger(GL13.GL_CLIENT_ACTIVE_TEXTURE);
        if (Compat.shadersEnabled()) {
            GL13.glClientActiveTexture(shaderTextureCoordsBinding);
        } else {
            GL13.glClientActiveTexture(fixedTextureUnitBinding);
        }

        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        GL13.glClientActiveTexture(lastClientActiveTexture);
    }

    public void setCoords(short block, short sky) {
        if (Compat.shadersEnabled()) {
            ARBMultitexture.glMultiTexCoord2sARB(shaderTextureCoordsBinding, block, sky);
        } else {
            ARBMultitexture.glMultiTexCoord2sARB(fixedTextureUnitBinding, block, sky);
        }
    }
}
