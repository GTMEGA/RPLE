/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.api.RPLEShadersAPI;
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


@SuppressWarnings("unused")
@Unique
@Mixin(ShadersTess.class)
public abstract class ShaderTessMixin {
    private static final int MIN_BUFFER_SIZE_INTS = 0x10000;
    private static final int MAX_BUFFER_SIZE_INTS = 0x1000000;

    private static final int TRIANGLE_VERTEX_COUNT = 3;
    private static final int QUAD_VERTEX_COUNT = 4;

    private final ShaderVertex rple$vertexA = new ShaderVertex();
    private final ShaderVertex rple$vertexB = new ShaderVertex();
    private final ShaderVertex rple$vertexC = new ShaderVertex();
    private final ShaderVertex rple$vertexD = new ShaderVertex();

    private IOptiFineTessellatorMixin rple$tessellator = null;

    //region TODO This stuff belongs in FalseTweaks

    @ModifyConstant(method = "draw",
                    remap = false,
                    constant = @Constant(intValue = 18))
    private static int strideSizeInts(int constant) {
        return VertexAPI.recomputeVertexInfo(18, 1);
    }

    @ModifyConstant(method = {"preDrawArray", "draw"},
                    remap = false,
                    constant = @Constant(intValue = 72))
    private static int strideSizeBytes(int constant) {
        return VertexAPI.recomputeVertexInfo(18, Float.BYTES);
    }

    private static int realDrawMode;

    @Inject(method = "draw",
            at = @At(value = "INVOKE",
                     target = "Ljava/lang/Math;min(II)I",
                     remap = false),
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD,
            require = 1)
    private static void preDrawLoop(Tessellator tess, CallbackInfoReturnable<Integer> cir, int voffset, int realDrawMode) {
        ShaderTessMixin.realDrawMode = realDrawMode;
    }

    @Redirect(method = "draw",
              at = @At(value = "INVOKE",
                       target = "Ljava/lang/Math;min(II)I",
                       remap = false),
              remap = false,
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
              remap = false,
              require = 1)
    private static void edgeTexCoordPreDraw(int index, int size, boolean normalized, int stride, FloatBuffer buffer) {
        stride = VertexAPI.recomputeVertexInfo(18, Float.BYTES);
        ARBVertexShader.glVertexAttribPointerARB(index, size, normalized, stride, buffer);
        if (RPLEShadersAPI.useRPLEEdgeTexCoordAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(RPLEShadersAPI.edgeTexCoordAttrib,
                                                     2,
                                                     false,
                                                     stride,
                                                     (FloatBuffer) buffer.position(VertexConstants.getRpleEdgeTexUIndexShader()));
            ARBVertexShader.glEnableVertexAttribArrayARB(RPLEShadersAPI.edgeTexCoordAttrib);
        }
    }

    @Inject(method = "postDrawArray",
            at = @At("RETURN"),
            remap = false,
            require = 1)
    private static void edgeTexCoordPostDraw(Tessellator tess, CallbackInfo ci) {
        if (RPLEShadersAPI.useRPLEEdgeTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(RPLEShadersAPI.edgeTexCoordAttrib);
        }
    }

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       remap = true,
                       ordinal = 0),
              remap = false,
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
              remap = false,
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

    @SuppressWarnings("CastToIncompatibleInterface")
    private static void enableLightMapTexture(Tessellator tessellator, int position, int unit) {
        val shortBuffer = ((IOptiFineTessellatorMixin) tessellator).rple$shortBuffer();

        OpenGlHelper.setClientActiveTexture(unit);
        shortBuffer.position(position);
        GL11.glTexCoordPointer(2, VertexAPI.recomputeVertexInfo(18, 4), shortBuffer);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static void disableLightMapTexture(int unit) {
        OpenGlHelper.setClientActiveTexture(unit);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite(remap = false)
    @SuppressWarnings("CastToIncompatibleInterface")
    public static void addVertex(Tessellator tessellator, double posX, double posY, double posZ) {
        val accessibleTessellator = (IOptiFineTessellatorMixin) tessellator;
        val shaderTessellator = (ShaderTessMixin) (Object) accessibleTessellator.rple$shaderTessellator();

        val floatPosX = (float) (posX + accessibleTessellator.rple$posXOffset());
        val floatPosY = (float) (posY + accessibleTessellator.rple$posYOffset());
        val floatPosZ = (float) (posZ + accessibleTessellator.rple$posZOffset());

        shaderTessellator.addVertex(accessibleTessellator, floatPosX, floatPosY, floatPosZ);
    }

    private void addVertex(IOptiFineTessellatorMixin tessellator, float posX, float posY, float posZ) {
        this.rple$tessellator = tessellator;

        val drawMode = tessellator.rple$drawMode();

        prepareBuffer(drawMode);

        if (drawMode == GL11.GL_TRIANGLES) {
            val vertexIndex = tessellator.rple$addedVertices() % TRIANGLE_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(rple$vertexA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(rple$vertexB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(rple$vertexC, posX, posY, posZ);
                default:
                    addTrianglePrimitive();
            }

            this.rple$tessellator = null;
        } else if (drawMode == GL11.GL_QUADS) {
            val vertexIndex = tessellator.rple$addedVertices() % QUAD_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(rple$vertexA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(rple$vertexB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(rple$vertexC, posX, posY, posZ);
                    break;
                case 3:
                    prepareVertex(rple$vertexD, posX, posY, posZ);
                default:
                    addQuadPrimitive();
            }

            this.rple$tessellator = null;
        } else {
            prepareVertex(rple$vertexA, posX, posY, posZ);
            addVertex(rple$vertexA);
        }
    }

    private void prepareBuffer(int drawMode) {
        val bufferSize = rple$tessellator.rple$bufferSize();

        if (rple$tessellator.rple$rawBufferIndex() + requiredSpaceInts() <= bufferSize)
            return;

        if (bufferSize < MIN_BUFFER_SIZE_INTS) {
            initBuffer();
            return;
        }

        final int vertexIndex;
        if (drawMode == GL11.GL_TRIANGLES) {
            vertexIndex = rple$tessellator.rple$addedVertices() % TRIANGLE_VERTEX_COUNT;
        } else if (drawMode == GL11.GL_QUADS) {
            vertexIndex = rple$tessellator.rple$addedVertices() % QUAD_VERTEX_COUNT;
        } else {
            vertexIndex = -1;
        }
        if (bufferSize >= MAX_BUFFER_SIZE_INTS && vertexIndex == 0) {
            earlyDraw();
            return;
        }

        extendBuffer();
    }

    private int requiredSpaceInts() {
        if (rple$tessellator.rple$drawMode() == GL11.GL_TRIANGLES) {
            return VertexAPI.recomputeVertexInfo(18, 3);
        } else if (rple$tessellator.rple$drawMode() == GL11.GL_QUADS) {
            return VertexAPI.recomputeVertexInfo(18, 4);
        } else {
            return VertexAPI.recomputeVertexInfo(18, 1);
        }
    }

    private void initBuffer() {
        rple$tessellator.rple$rawBuffer(new int[MIN_BUFFER_SIZE_INTS]);
        rple$tessellator.rple$bufferSize(MIN_BUFFER_SIZE_INTS);
    }

    private void earlyDraw() {
        rple$tessellator.rple$draw();
        rple$tessellator.rple$isDrawing(true);
    }

    private void extendBuffer() {
        val newBufferSize = rple$tessellator.rple$bufferSize() * 2;
        val oldRawBuffer = rple$tessellator.rple$rawBuffer();
        val newRawBuffer = Arrays.copyOf(oldRawBuffer, newBufferSize);

        rple$tessellator.rple$bufferSize(newBufferSize);
        rple$tessellator.rple$rawBuffer(newRawBuffer);

        SMCLog.info("Expand tessellator buffer %d", rple$tessellator.rple$bufferSize());
    }

    private void prepareVertex(ShaderVertex vertex, float posX, float posY, float posZ) {
        val brightness = rple$tessellator.rple$brightness();

        vertex.positionX(posX)
              .positionY(posY)
              .positionZ(posZ)
              .textureU(rple$tessellator.rple$textureU())
              .textureV(rple$tessellator.rple$textureV())
              .colorARGB(rple$tessellator.rple$color())
              .redLightMapUV(TessellatorBrightnessHelper.getBrightnessRed(brightness))
              .greenLightMapUV(TessellatorBrightnessHelper.getBrightnessGreen(brightness))
              .blueLightMapUV(TessellatorBrightnessHelper.getBrightnessBlue(brightness))
              .entityData(Shaders.getEntityData())
              .entityData2(Shaders.getEntityData2());

        rple$tessellator.rple$incrementAddedVertices(1);
        rple$tessellator.rple$incrementVertexCount(1);
    }

    private void addTrianglePrimitive() {
        calculateTriangleNormal();
        calculateTangent();
        calculateTriangleMidAndEdgeTextureUV();

        addVertex(rple$vertexA);
        addVertex(rple$vertexB);
        addVertex(rple$vertexC);
    }

    private void addQuadPrimitive() {
        calculateQuadNormal();
        calculateTangent();
        calculateQuadMidAndEdgeTextureUV();

        addVertex(rple$vertexA);
        addVertex(rple$vertexB);
        addVertex(rple$vertexC);
        addVertex(rple$vertexD);
    }

    private void calculateTriangleNormal() {
        val aPositionX = rple$vertexA.positionX();
        val aPositionY = rple$vertexA.positionY();
        val aPositionZ = rple$vertexA.positionZ();

        val bPositionX = rple$vertexB.positionX();
        val bPositionY = rple$vertexB.positionY();
        val bPositionZ = rple$vertexB.positionZ();

        val cPositionX = rple$vertexC.positionX();
        val cPositionY = rple$vertexC.positionY();
        val cPositionZ = rple$vertexC.positionZ();

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

        rple$vertexA.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);
        rple$vertexB.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);
        rple$vertexC.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);

        rple$tessellator.rple$hasNormals(true);
    }

    private void calculateQuadNormal() {
        val aPositionX = rple$vertexA.positionX();
        val aPositionY = rple$vertexA.positionY();
        val aPositionZ = rple$vertexA.positionZ();

        val bPositionX = rple$vertexB.positionX();
        val bPositionY = rple$vertexB.positionY();
        val bPositionZ = rple$vertexB.positionZ();

        val cPositionX = rple$vertexC.positionX();
        val cPositionY = rple$vertexC.positionY();
        val cPositionZ = rple$vertexC.positionZ();

        val dPositionX = rple$vertexD.positionX();
        val dPositionY = rple$vertexD.positionY();
        val dPositionZ = rple$vertexD.positionZ();

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

        rple$vertexA.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);
        rple$vertexB.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);
        rple$vertexC.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);
        rple$vertexD.normalX(normalX)
                    .normalY(normalY)
                    .normalZ(normalZ);

        rple$tessellator.rple$hasNormals(true);
    }

    private void calculateTangent() {
        val aPositionX = rple$vertexA.positionX();
        val aPositionY = rple$vertexA.positionY();
        val aPositionZ = rple$vertexA.positionZ();

        val bPositionX = rple$vertexB.positionX();
        val bPositionY = rple$vertexB.positionY();
        val bPositionZ = rple$vertexB.positionZ();

        val cPositionX = rple$vertexC.positionX();
        val cPositionY = rple$vertexC.positionY();
        val cPositionZ = rple$vertexC.positionZ();

        val normalX = rple$vertexA.normalX();
        val normalY = rple$vertexA.normalY();
        val normalZ = rple$vertexA.normalZ();

        val aTextureU = rple$vertexA.textureU();
        val aTextureV = rple$vertexA.textureV();

        val bTextureU = rple$vertexB.textureU();
        val bTextureV = rple$vertexB.textureV();

        val cTextureU = rple$vertexC.textureU();
        val cTextureV = rple$vertexC.textureV();

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

        if (rple$tessellator.rple$drawMode() == GL11.GL_TRIANGLES) {
            rple$vertexA.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
            rple$vertexB.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
            rple$vertexC.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
        } else if (rple$tessellator.rple$drawMode() == GL11.GL_QUADS) {
            rple$vertexA.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
            rple$vertexB.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
            rple$vertexC.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
            rple$vertexD.tangentX(tangentX)
                        .tangentY(tangentY)
                        .tangentZ(tangentZ)
                        .tangentW(tangentW);
        }
    }

    private static float safeSqrt(float value) {
        return value != 0F ? (float) Math.sqrt(value) : 1F;
    }

    private void calculateTriangleMidAndEdgeTextureUV() {
        val minU = min(rple$vertexA.textureU(), rple$vertexB.textureU(), rple$vertexC.textureU());
        val minV = min(rple$vertexA.textureV(), rple$vertexB.textureV(), rple$vertexC.textureV());
        val maxU = max(rple$vertexA.textureU(), rple$vertexB.textureU(), rple$vertexC.textureU());
        val maxV = max(rple$vertexA.textureV(), rple$vertexB.textureV(), rple$vertexC.textureV());

        rple$vertexA.edgeTextureU(minU);
        rple$vertexB.edgeTextureU(minU);
        rple$vertexC.edgeTextureU(minU);

        rple$vertexA.edgeTextureV(minV);
        rple$vertexB.edgeTextureV(minV);
        rple$vertexC.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        rple$vertexA.midTextureU(midU);
        rple$vertexB.midTextureU(midU);
        rple$vertexC.midTextureU(midU);

        rple$vertexA.midTextureV(midV);
        rple$vertexB.midTextureV(midV);
        rple$vertexC.midTextureV(midV);
    }

    private static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }

    private static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    private void calculateQuadMidAndEdgeTextureUV() {
        val minU = min(rple$vertexA.textureU(), rple$vertexB.textureU(), rple$vertexC.textureU(), rple$vertexD.textureU());
        val minV = min(rple$vertexA.textureV(), rple$vertexB.textureV(), rple$vertexC.textureV(), rple$vertexD.textureV());
        val maxU = max(rple$vertexA.textureU(), rple$vertexB.textureU(), rple$vertexC.textureU(), rple$vertexD.textureU());
        val maxV = max(rple$vertexA.textureV(), rple$vertexB.textureV(), rple$vertexC.textureV(), rple$vertexD.textureV());

        rple$vertexA.edgeTextureU(minU);
        rple$vertexB.edgeTextureU(minU);
        rple$vertexC.edgeTextureU(minU);
        rple$vertexD.edgeTextureU(minU);

        rple$vertexA.edgeTextureV(minV);
        rple$vertexB.edgeTextureV(minV);
        rple$vertexC.edgeTextureV(minV);
        rple$vertexD.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        rple$vertexA.midTextureU(midU);
        rple$vertexB.midTextureU(midU);
        rple$vertexC.midTextureU(midU);
        rple$vertexD.midTextureU(midU);

        rple$vertexA.midTextureV(midV);
        rple$vertexB.midTextureV(midV);
        rple$vertexC.midTextureV(midV);
        rple$vertexD.midTextureV(midV);
    }

    private static float min(float a, float b, float c, float d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    private static float max(float a, float b, float c, float d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    private void addVertex(ShaderVertex vertex) {
        vertex.toIntArray(rple$tessellator.rple$rawBufferIndex(), rple$tessellator.rple$rawBuffer());

        rple$tessellator.rple$incrementRawBufferIndex(VertexAPI.recomputeVertexInfo(18, 1));
    }
}
