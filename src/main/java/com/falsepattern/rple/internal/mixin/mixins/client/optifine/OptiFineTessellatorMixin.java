package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.rple.internal.mixin.interfaces.IOptiFineTessellatorMixin;
import net.minecraft.client.renderer.Tessellator;

import com.falsepattern.rple.internal.mixin.interfaces.ITessellatorJunction;
import org.spongepowered.asm.mixin.*;
import shadersmod.client.ShadersTess;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

@Mixin(Tessellator.class)
public abstract class OptiFineTessellatorMixin implements IOptiFineTessellatorMixin, ITessellatorJunction {
    /**
     * NOTE: OptiFine changes the buffer fields in the {@link Tessellator} from static to class fields.
     */
    @Shadow(aliases = "field_78394_d")
    private ByteBuffer byteBuffer;
    @Shadow(aliases = "field_147568_c")
    private IntBuffer intBuffer;
    @Shadow(aliases = "field_147566_d")
    private FloatBuffer floatBuffer;
    @Shadow(aliases = "field_147567_e")
    private ShortBuffer shortBuffer;

    @Shadow
    private int[] rawBuffer;
    @Shadow(aliases = "field_78388_E")
    private int bufferSize;
    @SuppressWarnings("MixinAnnotationTarget")
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

    // region Accessors
    @Override
    public ByteBuffer byteBuffer() {
        return byteBuffer;
    }

    @Override
    public IntBuffer intBuffer() {
        return intBuffer;
    }

    @Override
    public FloatBuffer floatBuffer() {
        return floatBuffer;
    }

    @Override
    public ShortBuffer shortBuffer() {
        return shortBuffer;
    }

    @Override
    public void rawBuffer(int[] rawBuffer) {
        this.rawBuffer = rawBuffer;
    }

    @Override
    public int[] rawBuffer() {
        return rawBuffer;
    }

    @Override
    public int bufferSize() {
        return bufferSize;
    }

    @Override
    public void bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void incrementRawBufferIndex(int increment) {
        rawBufferIndex += increment;
    }

    @Override
    public int rawBufferIndex() {
        return rawBufferIndex;
    }

    @Override
    public int drawMode() {
        return drawMode;
    }

    @Shadow
    public abstract int draw();

    @Override
    public int v$draw() {
        return draw();
    }

    @Override
    public void isDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    @Override
    public boolean isDrawing() {
        return isDrawing;
    }

    @Override
    public void incrementAddedVertices(int increment) {
        addedVertices += increment;
    }

    @Override
    public int addedVertices() {
        return addedVertices;
    }

    @Override
    public void incrementVertexCount(int increment) {
        vertexCount += increment;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public double posXOffset() {
        return xOffset;
    }

    @Override
    public double posYOffset() {
        return yOffset;
    }

    @Override
    public double posZOffset() {
        return zOffset;
    }

    @Override
    public double textureU() {
        return textureU;
    }

    @Override
    public double textureV() {
        return textureV;
    }

    @Override
    public int color() {
        return color;
    }

    @Override
    public void hasBrightness(boolean hasBrightness) {
        this.hasBrightness = hasBrightness;
    }

    @Override
    public boolean hasBrightness() {
        return hasBrightness;
    }

    @Override
    public void hasNormals(boolean hasNormals) {
        this.hasNormals = hasNormals;
    }

    @Override
    public boolean hasNormals() {
        return hasNormals;
    }

    @Override
    public ShadersTess shaderTessellator() {
        return shadersTess;
    }

    // endregion


    @Override
    public ShortBuffer RPLEgetShortBuffer() {
        return shortBuffer;
    }
}
