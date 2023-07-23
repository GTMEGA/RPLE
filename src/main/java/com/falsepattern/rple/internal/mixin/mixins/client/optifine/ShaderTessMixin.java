/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.api.client.RPLEShaderConstants;
import com.falsepattern.rple.internal.client.lightmap.LightMapHook;
import com.falsepattern.rple.internal.client.render.TessellatorBrightnessHelper;
import com.falsepattern.rple.internal.client.render.VertexConstants;
import com.falsepattern.rple.internal.mixin.extension.ShaderVertex;
import com.falsepattern.rple.internal.mixin.interfaces.IOptiFineTessellatorMixin;
import lombok.val;
import lombok.var;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersTess;
import shadersmod.common.SMCLog;

import java.nio.FloatBuffer;
import java.util.Arrays;


@Mixin(value = ShadersTess.class, remap = false)
public abstract class ShaderTessMixin {
    @Unique
    private static final int MIN_BUFFER_SIZE_INTS = 0x10000;
    @Unique
    private static final int MAX_BUFFER_SIZE_INTS = 0x1000000;
    @Unique
    private static final int TRIANGLE_VERTEX_COUNT = 3;
    @Unique
    private static final int QUAD_VERTEX_COUNT = 4;

    @Unique
    private final ShaderVertex vertA = new ShaderVertex();
    @Unique
    private final ShaderVertex vertB = new ShaderVertex();
    @Unique
    private final ShaderVertex vertC = new ShaderVertex();
    @Unique
    private final ShaderVertex vertD = new ShaderVertex();

    @Unique
    private IOptiFineTessellatorMixin ofTess = null;

    //region TODO This stuff belongs in FalseTweaks

    @ModifyConstant(method = "draw",
                    constant = @Constant(intValue = 18))
    private static int strideSizeInts(int constant) {
        return VertexAPI.recomputeVertexInfo(18, 1);
    }

    @ModifyConstant(method = {"preDrawArray", "draw"},
                    constant = @Constant(intValue = 72))
    private static int strideSizeBytes(int constant) {
        return VertexAPI.recomputeVertexInfo(18, Float.BYTES);
    }

    private static int realDrawMode;

    @Inject(method = "draw",
            at = @At(value = "INVOKE",
                     target = "Ljava/lang/Math;min(II)I"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1)
    private static void preDrawLoop(Tessellator tess, CallbackInfoReturnable<Integer> cir, int voffset, int realDrawMode) {
        ShaderTessMixin.realDrawMode = realDrawMode;
    }

    @Redirect(method = "draw",
              at = @At(value = "INVOKE",
                       target = "Ljava/lang/Math;min(II)I"),
              require = 1)
    private static int minWarn(int a, int b) {
        int vCount = Math.min(a, b);
        if (realDrawMode == GL11.GL_TRIANGLES) {
            vCount = vCount / 3 * 3;
        }
        return vCount;
    }

    //endregion

    @Redirect(method = "preDrawArray",
              at = @At(value = "INVOKE",
                       target = "Lorg/lwjgl/opengl/ARBVertexShader;glVertexAttribPointerARB(IIZILjava/nio/FloatBuffer;)V",
                       ordinal = 0),
              slice = @Slice(from = @At(value = "FIELD",
                                        target = "Lshadersmod/client/Shaders;useMidTexCoordAttrib:Z")),
              require = 1)
    private static void edgeTexCoordPreDraw(int index, int size, boolean normalized, int stride, FloatBuffer buffer) {
        stride = VertexAPI.recomputeVertexInfo(18, Float.BYTES);
        ARBVertexShader.glVertexAttribPointerARB(index, size, normalized, stride, buffer);
        if (RPLEShaderConstants.useRPLEEdgeTexCoordAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(RPLEShaderConstants.edgeTexCoordAttrib,
                                                     2,
                                                     false,
                                                     stride,
                                                     (FloatBuffer) buffer.position(VertexConstants.getRpleEdgeTexUIndexShader()));
            ARBVertexShader.glEnableVertexAttribArrayARB(RPLEShaderConstants.edgeTexCoordAttrib);
        }
    }

    @Inject(method = "postDrawArray",
            at = @At("RETURN"),
            require = 1)
    private static void edgeTexCoordPostDraw(Tessellator tess, CallbackInfo ci) {
        if (RPLEShaderConstants.useRPLEEdgeTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(RPLEShaderConstants.edgeTexCoordAttrib);
        }
    }

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       remap = true,
                       ordinal = 0),
              require = 1)
    @SuppressWarnings("CastToIncompatibleInterface")
    private static boolean enableLightMaps(Tessellator tessellator) {
        val hasBrightness = ((IOptiFineTessellatorMixin) tessellator).rple$hasBrightness();

        if (hasBrightness) {
            enableLightMapTexture(tessellator, VertexConstants.getRedIndexShader() * 2, LightMapHook.RED_LIGHT_MAP.textureUnit);
            enableLightMapTexture(tessellator, VertexConstants.getGreenIndexShader() * 2, LightMapHook.GREEN_LIGHT_MAP.textureUnit);
            enableLightMapTexture(tessellator, VertexConstants.getBlueIndexShader() * 2, LightMapHook.BLUE_LIGHT_MAP.textureUnit);
        }

        return false;
    }

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       remap = true,
                       ordinal = 1),
              require = 1)
    @SuppressWarnings("CastToIncompatibleInterface")
    private static boolean disableLightMaps(Tessellator tessellator) {
        val hasBrightness = ((IOptiFineTessellatorMixin) tessellator).rple$hasBrightness();

        if (hasBrightness) {
            disableLightMapTexture(LightMapHook.RED_LIGHT_MAP.textureUnit);
            disableLightMapTexture(LightMapHook.GREEN_LIGHT_MAP.textureUnit);
            disableLightMapTexture(LightMapHook.BLUE_LIGHT_MAP.textureUnit);
        }

        return false;
    }

    @Unique
    @SuppressWarnings("CastToIncompatibleInterface")
    private static void enableLightMapTexture(Tessellator tessellator, int position, int unit) {
        val shortBuffer = ((IOptiFineTessellatorMixin) tessellator).rple$shortBuffer();

        OpenGlHelper.setClientActiveTexture(unit);
        shortBuffer.position(position);
        GL11.glTexCoordPointer(2, VertexAPI.recomputeVertexInfo(18, 4), shortBuffer);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Unique
    private static void disableLightMapTexture(int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite
    @SuppressWarnings("CastToIncompatibleInterface")
    public static void addVertex(Tessellator tess, double posX, double posY, double posZ) {
        val ofTess = (IOptiFineTessellatorMixin) tess;
        val shaderTess = (ShaderTessMixin) (Object) ofTess.rple$shaderTessellator();

        val fPosX = (float) (posX + ofTess.rple$posXOffset());
        val fPosY = (float) (posY + ofTess.rple$posYOffset());
        val fPosZ = (float) (posZ + ofTess.rple$posZOffset());

        shaderTess.addVertex(ofTess, fPosX, fPosY, fPosZ);
    }

    @Unique
    private void addVertex(IOptiFineTessellatorMixin ofTess, float posX, float posY, float posZ) {
        this.ofTess = ofTess;

        val drawMode = ofTess.rple$drawMode();

        prepareBuffer(drawMode);

        if (drawMode == GL11.GL_TRIANGLES) {
            val vertexIndex = ofTess.rple$addedVertices() % TRIANGLE_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(vertA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(vertB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(vertC, posX, posY, posZ);
                default:
                    addTrianglePrimitive();
            }

            this.ofTess = null;
        } else if (drawMode == GL11.GL_QUADS) {
            val vertexIndex = ofTess.rple$addedVertices() % QUAD_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(vertA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(vertB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(vertC, posX, posY, posZ);
                    break;
                case 3:
                    prepareVertex(vertD, posX, posY, posZ);
                default:
                    addQuadPrimitive();
            }

            this.ofTess = null;
        } else {
            prepareVertex(vertA, posX, posY, posZ);
            addVertex(vertA);
        }
    }

    @Unique
    private void prepareBuffer(int drawMode) {
        val bufferSize = ofTess.rple$bufferSize();

        if (ofTess.rple$rawBufferIndex() + requiredSpaceInts() <= bufferSize)
            return;

        if (bufferSize < MIN_BUFFER_SIZE_INTS) {
            initBuffer();
            return;
        }

        final int vertexIndex;
        if (drawMode == GL11.GL_TRIANGLES) {
            vertexIndex = ofTess.rple$addedVertices() % TRIANGLE_VERTEX_COUNT;
        } else if (drawMode == GL11.GL_QUADS) {
            vertexIndex = ofTess.rple$addedVertices() % QUAD_VERTEX_COUNT;
        } else {
            vertexIndex = -1;
        }
        if (bufferSize >= MAX_BUFFER_SIZE_INTS && vertexIndex == 0) {
            earlyDraw();
            return;
        }

        extendBuffer();
    }

    @Unique
    private int requiredSpaceInts() {
        if (ofTess.rple$drawMode() == GL11.GL_TRIANGLES) {
            return VertexAPI.recomputeVertexInfo(18, 3);
        } else if (ofTess.rple$drawMode() == GL11.GL_QUADS) {
            return VertexAPI.recomputeVertexInfo(18, 4);
        } else {
            return VertexAPI.recomputeVertexInfo(18, 1);
        }
    }

    @Unique
    private void initBuffer() {
        ofTess.rple$rawBuffer(new int[MIN_BUFFER_SIZE_INTS]);
        ofTess.rple$bufferSize(MIN_BUFFER_SIZE_INTS);
    }

    @Unique
    private void earlyDraw() {
        ofTess.rple$draw();
        ofTess.rple$isDrawing(true);
    }

    @Unique
    private void extendBuffer() {
        val newBufferSize = ofTess.rple$bufferSize() * 2;
        val oldRawBuffer = ofTess.rple$rawBuffer();
        val newRawBuffer = Arrays.copyOf(oldRawBuffer, newBufferSize);

        ofTess.rple$bufferSize(newBufferSize);
        ofTess.rple$rawBuffer(newRawBuffer);

        SMCLog.info("Expand tessellator buffer %d", ofTess.rple$bufferSize());
    }

    @Unique
    private void prepareVertex(ShaderVertex vertex, float posX, float posY, float posZ) {
        val packed = ofTess.rple$packedBrightness();

        vertex.positionX(posX)
              .positionY(posY)
              .positionZ(posZ)
              .textureU(ofTess.rple$textureU())
              .textureV(ofTess.rple$textureV())
              .colorARGB(ofTess.rple$color())
              .redLightMapUV(TessellatorBrightnessHelper.getBrightnessRed(packed))
              .greenLightMapUV(TessellatorBrightnessHelper.getBrightnessGreen(packed))
              .blueLightMapUV(TessellatorBrightnessHelper.getBrightnessBlue(packed))
              .entityData(Shaders.getEntityData())
              .entityData2(Shaders.getEntityData2());

        ofTess.rple$incrementAddedVertices(1);
        ofTess.rple$incrementVertexCount(1);
    }

    @Unique
    private void addTrianglePrimitive() {
        calculateTriangleNormal();
        calculateTangent();
        calculateTriangleMidAndEdgeTextureUV();

        addVertex(vertA);
        addVertex(vertB);
        addVertex(vertC);
    }

    @Unique
    private void addQuadPrimitive() {
        calculateQuadNormal();
        calculateTangent();
        calculateQuadMidAndEdgeTextureUV();

        addVertex(vertA);
        addVertex(vertB);
        addVertex(vertC);
        addVertex(vertD);
    }

    @Unique
    private void calculateTriangleNormal() {
        val aPositionX = vertA.positionX();
        val aPositionY = vertA.positionY();
        val aPositionZ = vertA.positionZ();

        val bPositionX = vertB.positionX();
        val bPositionY = vertB.positionY();
        val bPositionZ = vertB.positionZ();

        val cPositionX = vertC.positionX();
        val cPositionY = vertC.positionY();
        val cPositionZ = vertC.positionZ();

        val acDeltaX = cPositionX - aPositionX;
        val acDeltaY = cPositionY - aPositionY;
        val acDeltaZ = cPositionZ - aPositionZ;

        val abDeltaX = aPositionX - bPositionX;
        val abDeltaY = aPositionY - bPositionY;
        val abDeltaZ = aPositionZ - bPositionZ;

        var normalX = acDeltaY * abDeltaZ - acDeltaZ * abDeltaY;
        var normalY = acDeltaZ * abDeltaX - acDeltaX * abDeltaZ;
        var normalZ = acDeltaX * abDeltaY - acDeltaY * abDeltaX;

        val length = safeSqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        normalX /= length;
        normalY /= length;
        normalZ /= length;

        vertA.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);
        vertB.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);
        vertC.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);

        ofTess.rple$hasNormals(true);
    }

    @Unique
    private void calculateQuadNormal() {
        val aPositionX = vertA.positionX();
        val aPositionY = vertA.positionY();
        val aPositionZ = vertA.positionZ();

        val bPositionX = vertB.positionX();
        val bPositionY = vertB.positionY();
        val bPositionZ = vertB.positionZ();

        val cPositionX = vertC.positionX();
        val cPositionY = vertC.positionY();
        val cPositionZ = vertC.positionZ();

        val dPositionX = vertD.positionX();
        val dPositionY = vertD.positionY();
        val dPositionZ = vertD.positionZ();

        val acDeltaX = cPositionX - aPositionX;
        val acDeltaY = cPositionY - aPositionY;
        val acDeltaZ = cPositionZ - aPositionZ;

        val dbDeltaX = dPositionX - bPositionX;
        val dbDeltaY = dPositionY - bPositionY;
        val dbDeltaZ = dPositionZ - bPositionZ;

        var normalX = acDeltaY * dbDeltaZ - acDeltaZ * dbDeltaY;
        var normalY = acDeltaZ * dbDeltaX - acDeltaX * dbDeltaZ;
        var normalZ = acDeltaX * dbDeltaY - acDeltaY * dbDeltaX;

        val length = safeSqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        normalX /= length;
        normalY /= length;
        normalZ /= length;

        vertA.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);
        vertB.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);
        vertC.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);
        vertD.normalX(normalX)
             .normalY(normalY)
             .normalZ(normalZ);

        ofTess.rple$hasNormals(true);
    }

    @Unique
    private void calculateTangent() {
        val aPositionX = vertA.positionX();
        val aPositionY = vertA.positionY();
        val aPositionZ = vertA.positionZ();

        val bPositionX = vertB.positionX();
        val bPositionY = vertB.positionY();
        val bPositionZ = vertB.positionZ();

        val cPositionX = vertC.positionX();
        val cPositionY = vertC.positionY();
        val cPositionZ = vertC.positionZ();

        val normalX = vertA.normalX();
        val normalY = vertA.normalY();
        val normalZ = vertA.normalZ();

        val aTextureU = vertA.textureU();
        val aTextureV = vertA.textureV();

        val bTextureU = vertB.textureU();
        val bTextureV = vertB.textureV();

        val cTextureU = vertC.textureU();
        val cTextureV = vertC.textureV();

        val abDeltaX = bPositionX - aPositionX;
        val abDeltaY = bPositionY - aPositionY;
        val abDeltaZ = bPositionZ - aPositionZ;

        val acDeltaX = cPositionX - aPositionX;
        val acDeltaY = cPositionY - aPositionY;
        val acDeltaZ = cPositionZ - aPositionZ;

        val abDeltaU = bTextureU - aTextureU;
        val abDeltaV = bTextureV - aTextureV;
        val acDeltaU = cTextureU - aTextureU;
        val acDeltaV = cTextureV - aTextureV;

        val deltaLengthSquared = abDeltaU * acDeltaV - acDeltaU * abDeltaV;

        final float deltaFactor;
        if (deltaLengthSquared == 0.0) {
            deltaFactor = 1.0f;
        } else {
            deltaFactor = 1.0f / deltaLengthSquared;
        }

        var tangentX = deltaFactor * (acDeltaV * abDeltaX - abDeltaV * acDeltaX);
        var tangentY = deltaFactor * (acDeltaV * abDeltaY - abDeltaV * acDeltaY);
        var tangentZ = deltaFactor * (acDeltaV * abDeltaZ - abDeltaV * acDeltaZ);

        val tangentLength = safeSqrt(tangentX * tangentX + tangentY * tangentY + tangentZ * tangentZ);
        tangentX /= tangentLength;
        tangentY /= tangentLength;
        tangentZ /= tangentLength;

        var biTangentX = deltaFactor * (-acDeltaU * abDeltaX + abDeltaU * acDeltaX);
        var biTangentY = deltaFactor * (-acDeltaU * abDeltaY + abDeltaU * acDeltaY);
        var biTangentZ = deltaFactor * (-acDeltaU * abDeltaZ + abDeltaU * acDeltaZ);

        val biTangentLength = safeSqrt(biTangentX * biTangentX + biTangentY * biTangentY + biTangentZ * biTangentZ);
        biTangentX /= biTangentLength;
        biTangentY /= biTangentLength;
        biTangentZ /= biTangentLength;

        val otherBiTangentX = tangentY * normalZ - tangentZ * normalY;
        val otherBiTangentY = tangentZ * normalX - tangentX * normalZ;
        val otherBiTangentZ = tangentX * normalY - tangentY * normalX;

        val tangentDotProduct = (biTangentX * otherBiTangentX) + (biTangentY * otherBiTangentY) + (biTangentZ * otherBiTangentZ);

        final float tangentW;
        if (tangentDotProduct < 0) {
            tangentW = -1.0F;
        } else {
            tangentW = 1.0F;
        }

        if (ofTess.rple$drawMode() == GL11.GL_TRIANGLES) {
            vertA.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
            vertB.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
            vertC.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
        } else if (ofTess.rple$drawMode() == GL11.GL_QUADS) {
            vertA.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
            vertB.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
            vertC.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
            vertD.tangentX(tangentX)
                 .tangentY(tangentY)
                 .tangentZ(tangentZ)
                 .tangentW(tangentW);
        }
    }

    @Unique
    private void calculateTriangleMidAndEdgeTextureUV() {
        val minU = min(vertA.textureU(), vertB.textureU(), vertC.textureU());
        val minV = min(vertA.textureV(), vertB.textureV(), vertC.textureV());
        val maxU = max(vertA.textureU(), vertB.textureU(), vertC.textureU());
        val maxV = max(vertA.textureV(), vertB.textureV(), vertC.textureV());

        vertA.edgeTextureU(minU);
        vertB.edgeTextureU(minU);
        vertC.edgeTextureU(minU);

        vertA.edgeTextureV(minV);
        vertB.edgeTextureV(minV);
        vertC.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        vertA.midTextureU(midU);
        vertB.midTextureU(midU);
        vertC.midTextureU(midU);

        vertA.midTextureV(midV);
        vertB.midTextureV(midV);
        vertC.midTextureV(midV);
    }

    @Unique
    private static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }

    @Unique
    private static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    @Unique
    private void calculateQuadMidAndEdgeTextureUV() {
        val minU = min(vertA.textureU(), vertB.textureU(), vertC.textureU(), vertD.textureU());
        val minV = min(vertA.textureV(), vertB.textureV(), vertC.textureV(), vertD.textureV());
        val maxU = max(vertA.textureU(), vertB.textureU(), vertC.textureU(), vertD.textureU());
        val maxV = max(vertA.textureV(), vertB.textureV(), vertC.textureV(), vertD.textureV());

        vertA.edgeTextureU(minU);
        vertB.edgeTextureU(minU);
        vertC.edgeTextureU(minU);
        vertD.edgeTextureU(minU);

        vertA.edgeTextureV(minV);
        vertB.edgeTextureV(minV);
        vertC.edgeTextureV(minV);
        vertD.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        vertA.midTextureU(midU);
        vertB.midTextureU(midU);
        vertC.midTextureU(midU);
        vertD.midTextureU(midU);

        vertA.midTextureV(midV);
        vertB.midTextureV(midV);
        vertC.midTextureV(midV);
        vertD.midTextureV(midV);
    }

    @Unique
    private void addVertex(ShaderVertex vertex) {
        vertex.toIntArray(ofTess.rple$rawBufferIndex(), ofTess.rple$rawBuffer());

        ofTess.rple$incrementRawBufferIndex(VertexAPI.recomputeVertexInfo(18, 1));
    }

    @Unique
    private static float min(float a, float b, float c, float d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    @Unique
    private static float max(float a, float b, float c, float d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    @Unique
    private static float safeSqrt(float value) {
        return value != 0F ? (float) Math.sqrt(value) : 1F;
    }
}
