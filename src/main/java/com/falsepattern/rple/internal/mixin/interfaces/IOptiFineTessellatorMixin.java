package com.falsepattern.rple.internal.mixin.interfaces;

import shadersmod.client.ShadersTess;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface IOptiFineTessellatorMixin {
    ByteBuffer byteBuffer();

    IntBuffer intBuffer();

    FloatBuffer floatBuffer();

    ShortBuffer shortBuffer();

    void rawBuffer(int[] rawBuffer);

    int[] rawBuffer();

    void bufferSize(int rawBufferSize);

    int bufferSize();

    default void incrementRawBufferIndex() {
        incrementRawBufferIndex(1);
    }

    void incrementRawBufferIndex(int increment);

    int rawBufferIndex();

    int drawMode();

    int v$draw();

    void isDrawing(boolean isDrawing);

    boolean isDrawing();

    default void incrementAddedVertices() {
        incrementAddedVertices(1);
    }

    void incrementAddedVertices(int increment);

    int addedVertices();

    default void incrementVertexCount() {
        incrementVertexCount(1);
    }

    void incrementVertexCount(int increment);

    int vertexCount();

    double posXOffset();

    double posYOffset();

    double posZOffset();

    double textureU();

    double textureV();

    int color();

    void hasBrightness(boolean hasBrightness);

    boolean hasBrightness();

    long brightness();

    void hasNormals(boolean hasNormals);

    boolean hasNormals();

    ShadersTess shaderTessellator();
}
