/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.interfaces;

import shadersmod.client.ShadersTess;

public interface IOptiFineTessellatorMixin extends ITessellatorMixin {
    void rple$rawBuffer(int[] rawBuffer);

    int[] rple$rawBuffer();

    void rple$bufferSize(int rawBufferSize);

    int rple$bufferSize();

    void rple$incrementRawBufferIndex(int increment);

    int rple$rawBufferIndex();

    int rple$drawMode();

    int rple$draw();

    void rple$isDrawing(boolean isDrawing);

    void rple$incrementAddedVertices(int increment);

    int rple$addedVertices();

    void rple$incrementVertexCount(int increment);

    double rple$posXOffset();

    double rple$posYOffset();

    double rple$posZOffset();

    double rple$textureU();

    double rple$textureV();

    int rple$color();

    boolean rple$hasBrightness();

    void rple$hasNormals(boolean hasNormals);

    ShadersTess rple$shaderTessellator();
}
