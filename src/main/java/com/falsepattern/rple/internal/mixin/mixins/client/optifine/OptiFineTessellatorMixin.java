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

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.internal.mixin.interfaces.IOptiFineTessellatorMixin;
import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import shadersmod.client.ShadersTess;

import java.nio.ShortBuffer;

@Unique
@Mixin(Tessellator.class)
public abstract class OptiFineTessellatorMixin implements IOptiFineTessellatorMixin {

    @Shadow(aliases = "field_147567_e")
    private ShortBuffer shortBuffer;

    @Shadow
    private int[] rawBuffer;
    @Shadow(aliases = "field_78388_E")
    private int bufferSize;
    @Dynamic
    @Shadow(remap = false)
    private ShadersTess shadersTess;
    @Shadow
    private int rawBufferIndex;
    @Shadow
    private int drawMode;
    @Shadow
    private boolean isDrawing;
    @Shadow
    private int addedVertices;
    @Shadow
    private int vertexCount;
    @Shadow
    private double xOffset;
    @Shadow
    private double yOffset;
    @Shadow
    private double zOffset;
    @Shadow
    private double textureU;
    @Shadow
    private double textureV;
    @Shadow
    private int color;
    @Shadow
    private boolean hasBrightness;
    @Shadow
    private boolean hasNormals;

    @Shadow
    public abstract int draw();

    // region Accessors
    @Override
    public void rple$rawBuffer(int[] rawBuffer) {
        this.rawBuffer = rawBuffer;
    }

    @Override
    public int[] rple$rawBuffer() {
        return rawBuffer;
    }

    @Override
    public int rple$bufferSize() {
        return bufferSize;
    }

    @Override
    public void rple$bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void rple$incrementRawBufferIndex(int increment) {
        rawBufferIndex += increment;
    }

    @Override
    public int rple$rawBufferIndex() {
        return rawBufferIndex;
    }

    @Override
    public int rple$drawMode() {
        return drawMode;
    }

    @Override
    public int rple$draw() {
        return draw();
    }

    @Override
    public void rple$isDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    @Override
    public void rple$incrementAddedVertices(int increment) {
        addedVertices += increment;
    }

    @Override
    public int rple$addedVertices() {
        return addedVertices;
    }

    @Override
    public void rple$incrementVertexCount(int increment) {
        vertexCount += increment;
    }

    @Override
    public double rple$posXOffset() {
        return xOffset;
    }

    @Override
    public double rple$posYOffset() {
        return yOffset;
    }

    @Override
    public double rple$posZOffset() {
        return zOffset;
    }

    @Override
    public double rple$textureU() {
        return textureU;
    }

    @Override
    public double rple$textureV() {
        return textureV;
    }

    @Override
    public int rple$color() {
        return color;
    }

    @Override
    public boolean rple$hasBrightness() {
        return hasBrightness;
    }

    @Override
    public void rple$hasNormals(boolean hasNormals) {
        this.hasNormals = hasNormals;
    }

    @Override
    public ShadersTess rple$shaderTessellator() {
        return shadersTess;
    }

    @Override
    public ShortBuffer rple$shortBuffer() {
        return shortBuffer;
    }
    // endregion
}
