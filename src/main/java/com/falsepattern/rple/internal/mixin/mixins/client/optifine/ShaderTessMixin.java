/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.RPLEShaders;
import com.falsepattern.rple.internal.RPLE;
import com.falsepattern.rple.internal.color.BrightnessUtil;
import com.falsepattern.rple.internal.lightmap.LightMapHook;
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
@Mixin(value = ShadersTess.class)
public abstract class ShaderTessMixin {
    private static final int MIN_BUFFER_SIZE_INTS = 0x10000;
    private static final int MAX_BUFFER_SIZE_INTS = 0x1000000;

    private static final int TRIANGLE_VERTEX_COUNT = 3;
    private static final int QUAD_VERTEX_COUNT = 4;

    private final ShaderVertex vertexA = new ShaderVertex();
    private final ShaderVertex vertexB = new ShaderVertex();
    private final ShaderVertex vertexC = new ShaderVertex();
    private final ShaderVertex vertexD = new ShaderVertex();

    private IOptiFineTessellatorMixin tessellator = null;

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
        if (RPLEShaders.useRPLEEdgeTexCoordAttrib) {
            ARBVertexShader.glVertexAttribPointerARB(RPLEShaders.edgeTexCoordAttrib, 2, false, stride, (FloatBuffer) buffer.position(RPLE.getRpleEdgeTexUIndexShader()));
            ARBVertexShader.glEnableVertexAttribArrayARB(RPLEShaders.edgeTexCoordAttrib);
        }
    }

    @Inject(method = "postDrawArray",
            at = @At(value = "RETURN"),
            remap = false,
            require = 1)
    private static void edgeTexCoordPostDraw(Tessellator tess, CallbackInfo ci) {
        if (RPLEShaders.useRPLEEdgeTexCoordAttrib && tess.hasTexture) {
            ARBVertexShader.glDisableVertexAttribArrayARB(RPLEShaders.edgeTexCoordAttrib);
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
    private static boolean enableLightMaps(Tessellator tessellator) {
        val hasBrightness = ((IOptiFineTessellatorMixin) tessellator).hasBrightness();

        if (hasBrightness) {
            enableLightMapTexture(tessellator, RPLE.getRedIndexShader() * 2, LightMapHook.RED_LIGHT_MAP.textureUnit);
            enableLightMapTexture(tessellator, RPLE.getGreenIndexShader() * 2, LightMapHook.GREEN_LIGHT_MAP.textureUnit);
            enableLightMapTexture(tessellator, RPLE.getBlueIndexShader() * 2, LightMapHook.BLUE_LIGHT_MAP.textureUnit);
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
    private static boolean disableLightMaps(Tessellator tessellator) {
        val hasBrightness = ((IOptiFineTessellatorMixin) tessellator).hasBrightness();

        if (hasBrightness) {
            disableLightMapTexture(LightMapHook.RED_LIGHT_MAP.textureUnit);
            disableLightMapTexture(LightMapHook.GREEN_LIGHT_MAP.textureUnit);
            disableLightMapTexture(LightMapHook.BLUE_LIGHT_MAP.textureUnit);
        }

        return false;
    }

    private static void enableLightMapTexture(Tessellator tessellator, int position, int unit) {
        val shortBuffer = ((IOptiFineTessellatorMixin) tessellator).shortBuffer();

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
    public static void addVertex(Tessellator tessellator, double posX, double posY, double posZ) {
        val accessibleTessellator = ((IOptiFineTessellatorMixin) tessellator);
        val shaderTessellator = (ShaderTessMixin) (Object) accessibleTessellator.shaderTessellator();

        val floatPosX = (float) (posX + accessibleTessellator.posXOffset());
        val floatPosY = (float) (posY + accessibleTessellator.posYOffset());
        val floatPosZ = (float) (posZ + accessibleTessellator.posZOffset());

        shaderTessellator.addVertex(accessibleTessellator, floatPosX, floatPosY, floatPosZ);
    }

    private void addVertex(IOptiFineTessellatorMixin tessellator, float posX, float posY, float posZ) {
        this.tessellator = tessellator;

        val drawMode = tessellator.drawMode();

        prepareBuffer(drawMode);

        if (drawMode == GL11.GL_TRIANGLES) {
            val vertexIndex = tessellator.addedVertices() % TRIANGLE_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(vertexA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(vertexB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(vertexC, posX, posY, posZ);
                default:
                    addTrianglePrimitive();
            }

            this.tessellator = null;
        } else if (drawMode == GL11.GL_QUADS) {
            val vertexIndex = tessellator.addedVertices() % QUAD_VERTEX_COUNT;

            switch (vertexIndex) {
                case 0:
                    prepareVertex(vertexA, posX, posY, posZ);
                    break;
                case 1:
                    prepareVertex(vertexB, posX, posY, posZ);
                    break;
                case 2:
                    prepareVertex(vertexC, posX, posY, posZ);
                    break;
                case 3:
                    prepareVertex(vertexD, posX, posY, posZ);
                default:
                    addQuadPrimitive();
            }

            this.tessellator = null;
        } else {
            prepareVertex(vertexA, posX, posY, posZ);
            addVertex(vertexA);
        }
    }

    private void prepareBuffer(int drawMode) {
        val bufferSize = tessellator.bufferSize();

        if (tessellator.rawBufferIndex() + requiredSpaceInts() <= bufferSize)
            return;

        if (bufferSize < MIN_BUFFER_SIZE_INTS) {
            initBuffer();
            return;
        }

        final int vertexIndex;
        if (drawMode == GL11.GL_TRIANGLES) {
            vertexIndex = tessellator.addedVertices() % TRIANGLE_VERTEX_COUNT;
        } else if (drawMode == GL11.GL_QUADS) {
            vertexIndex = tessellator.addedVertices() % QUAD_VERTEX_COUNT;
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
        if (tessellator.drawMode() == GL11.GL_TRIANGLES) {
            return VertexAPI.recomputeVertexInfo(18, 3);
        } else if (tessellator.drawMode() == GL11.GL_QUADS) {
            return VertexAPI.recomputeVertexInfo(18, 4);
        } else {
            return VertexAPI.recomputeVertexInfo(18, 1);
        }
    }

    private void initBuffer() {
        tessellator.rawBuffer(new int[MIN_BUFFER_SIZE_INTS]);
        tessellator.bufferSize(MIN_BUFFER_SIZE_INTS);
    }

    private void earlyDraw() {
        tessellator.v$draw();
        tessellator.isDrawing(true);
    }

    private void extendBuffer() {
        val newBufferSize = tessellator.bufferSize() * 2;
        val oldRawBuffer = tessellator.rawBuffer();
        val newRawBuffer = Arrays.copyOf(oldRawBuffer, newBufferSize);

        tessellator.bufferSize(newBufferSize);
        tessellator.rawBuffer(newRawBuffer);

        SMCLog.info("Expand tessellator buffer %d", tessellator.bufferSize());
    }

    private void prepareVertex(ShaderVertex vertex, float posX, float posY, float posZ) {
        val brightness = tessellator.brightness();

        vertex.positionX(posX)
              .positionY(posY)
              .positionZ(posZ)
              .textureU(tessellator.textureU())
              .textureV(tessellator.textureV())
              .colorARGB(tessellator.color())
              .redLightMapUV(BrightnessUtil.getBrightnessRed(brightness))
              .greenLightMapUV(BrightnessUtil.getBrightnessGreen(brightness))
              .blueLightMapUV(BrightnessUtil.getBrightnessBlue(brightness))
              .entityData(Shaders.getEntityData())
              .entityData2(Shaders.getEntityData2());

        tessellator.incrementAddedVertices();
        tessellator.incrementVertexCount();
    }

    private void addTrianglePrimitive() {
        calculateTriangleNormal();
        calculateTangent();
        calculateTriangleMidAndEdgeTextureUV();

        addVertex(vertexA);
        addVertex(vertexB);
        addVertex(vertexC);
    }

    private void addQuadPrimitive() {
        calculateQuadNormal();
        calculateTangent();
        calculateQuadMidAndEdgeTextureUV();

        addVertex(vertexA);
        addVertex(vertexB);
        addVertex(vertexC);
        addVertex(vertexD);
    }

    private void calculateTriangleNormal() {
        val aPositionX = vertexA.positionX();
        val aPositionY = vertexA.positionY();
        val aPositionZ = vertexA.positionZ();

        val bPositionX = vertexB.positionX();
        val bPositionY = vertexB.positionY();
        val bPositionZ = vertexB.positionZ();

        val cPositionX = vertexC.positionX();
        val cPositionY = vertexC.positionY();
        val cPositionZ = vertexC.positionZ();

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

        vertexA.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);
        vertexB.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);
        vertexC.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);

        tessellator.hasNormals(true);
    }

    private void calculateQuadNormal() {
        val aPositionX = vertexA.positionX();
        val aPositionY = vertexA.positionY();
        val aPositionZ = vertexA.positionZ();

        val bPositionX = vertexB.positionX();
        val bPositionY = vertexB.positionY();
        val bPositionZ = vertexB.positionZ();

        val cPositionX = vertexC.positionX();
        val cPositionY = vertexC.positionY();
        val cPositionZ = vertexC.positionZ();

        val dPositionX = vertexD.positionX();
        val dPositionY = vertexD.positionY();
        val dPositionZ = vertexD.positionZ();

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

        vertexA.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);
        vertexB.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);
        vertexC.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);
        vertexD.normalX(normalX)
               .normalY(normalY)
               .normalZ(normalZ);

        tessellator.hasNormals(true);
    }

    private void calculateTangent() {
        val aPositionX = vertexA.positionX();
        val aPositionY = vertexA.positionY();
        val aPositionZ = vertexA.positionZ();

        val bPositionX = vertexB.positionX();
        val bPositionY = vertexB.positionY();
        val bPositionZ = vertexB.positionZ();

        val cPositionX = vertexC.positionX();
        val cPositionY = vertexC.positionY();
        val cPositionZ = vertexC.positionZ();

        val normalX = vertexA.normalX();
        val normalY = vertexA.normalY();
        val normalZ = vertexA.normalZ();

        val aTextureU = vertexA.textureU();
        val aTextureV = vertexA.textureV();

        val bTextureU = vertexB.textureU();
        val bTextureV = vertexB.textureV();

        val cTextureU = vertexC.textureU();
        val cTextureV = vertexC.textureV();

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

        if (tessellator.drawMode() == GL11.GL_TRIANGLES) {
            vertexA.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
            vertexB.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
            vertexC.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
        } else if (tessellator.drawMode() == GL11.GL_QUADS) {
            vertexA.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
            vertexB.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
            vertexC.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
            vertexD.tangentX(tangentX)
                   .tangentY(tangentY)
                   .tangentZ(tangentZ)
                   .tangentW(tangentW);
        }
    }

    private static float safeSqrt(float value) {
        return value != 0F ? (float) Math.sqrt(value) : 1F;
    }

    private void calculateTriangleMidAndEdgeTextureUV() {
        val minU = min(vertexA.textureU(), vertexB.textureU(), vertexC.textureU());
        val minV = min(vertexA.textureV(), vertexB.textureV(), vertexC.textureV());
        val maxU = max(vertexA.textureU(), vertexB.textureU(), vertexC.textureU());
        val maxV = max(vertexA.textureV(), vertexB.textureV(), vertexC.textureV());

        vertexA.edgeTextureU(minU);
        vertexB.edgeTextureU(minU);
        vertexC.edgeTextureU(minU);

        vertexA.edgeTextureV(minV);
        vertexB.edgeTextureV(minV);
        vertexC.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        vertexA.midTextureU(midU);
        vertexB.midTextureU(midU);
        vertexC.midTextureU(midU);

        vertexA.midTextureV(midV);
        vertexB.midTextureV(midV);
        vertexC.midTextureV(midV);
    }

    private static float min(float a, float b, float c) {
        return Math.min(Math.min(a, b), c);
    }
    private static float max(float a, float b, float c) {
        return Math.max(Math.max(a, b), c);
    }

    private void calculateQuadMidAndEdgeTextureUV() {
        val minU = min(vertexA.textureU(), vertexB.textureU(), vertexC.textureU(), vertexD.textureU());
        val minV = min(vertexA.textureV(), vertexB.textureV(), vertexC.textureV(), vertexD.textureV());
        val maxU = max(vertexA.textureU(), vertexB.textureU(), vertexC.textureU(), vertexD.textureU());
        val maxV = max(vertexA.textureV(), vertexB.textureV(), vertexC.textureV(), vertexD.textureV());

        vertexA.edgeTextureU(minU);
        vertexB.edgeTextureU(minU);
        vertexC.edgeTextureU(minU);
        vertexD.edgeTextureU(minU);

        vertexA.edgeTextureV(minV);
        vertexB.edgeTextureV(minV);
        vertexC.edgeTextureV(minV);
        vertexD.edgeTextureV(minV);

        val midU = (minU + maxU) / 2;
        val midV = (minV + maxV) / 2;

        vertexA.midTextureU(midU);
        vertexB.midTextureU(midU);
        vertexC.midTextureU(midU);
        vertexD.midTextureU(midU);

        vertexA.midTextureV(midV);
        vertexB.midTextureV(midV);
        vertexC.midTextureV(midV);
        vertexD.midTextureV(midV);
    }

    private static float min(float a, float b, float c, float d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }
    private static float max(float a, float b, float c, float d) {
        return Math.max(Math.max(a, b), Math.max(c, d));
    }

    private void addVertex(ShaderVertex vertex) {
        vertex.toIntArray(tessellator.rawBufferIndex(), tessellator.rawBuffer());

        tessellator.incrementRawBufferIndex(VertexAPI.recomputeVertexInfo(18, 1));
    }
}
