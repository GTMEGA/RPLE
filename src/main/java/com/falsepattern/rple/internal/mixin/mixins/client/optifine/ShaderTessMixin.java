package com.falsepattern.rple.internal.mixin.mixins.client.optifine;

import com.falsepattern.falsetweaks.api.triangulator.VertexAPI;
import com.falsepattern.rple.internal.RPLE;
import com.falsepattern.rple.internal.Utils;
import com.falsepattern.rple.internal.lightmap.LightMapHook;
import com.falsepattern.rple.internal.mixin.extension.ShaderVertex;
import com.falsepattern.rple.internal.mixin.interfaces.IOptiFineTessellatorMixin;
import lombok.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersTess;
import shadersmod.common.SMCLog;

import java.util.Arrays;

import static com.falsepattern.rple.internal.mixin.extension.ShaderVertex.VERTEX_STRIDE_INTS;

@SuppressWarnings("unused")
@Mixin(ShadersTess.class)
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

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       ordinal = 0),
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
                       ordinal = 1),
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
    @Overwrite
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

        if (drawMode != GL11.GL_TRIANGLES && drawMode != GL11.GL_QUADS)
            return;

        prepareBuffer();

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
        } else {
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
        }
    }

    private void prepareBuffer() {
        val bufferSize = tessellator.bufferSize();

        if (tessellator.rawBufferIndex() + requiredSpaceInts() <= bufferSize)
            return;

        if (bufferSize < MIN_BUFFER_SIZE_INTS) {
            initBuffer();
            return;
        }

        if (bufferSize >= MAX_BUFFER_SIZE_INTS) {
            earlyDraw();
            return;
        }

        extendBuffer();
    }

    private int requiredSpaceInts() {
        if (tessellator.drawMode() == GL11.GL_TRIANGLES) {
            return TRIANGLE_VERTEX_COUNT * VERTEX_STRIDE_INTS;
        } else if (tessellator.drawMode() == GL11.GL_QUADS) {
            return QUAD_VERTEX_COUNT * VERTEX_STRIDE_INTS;
        }

        throw new IllegalStateException("Invalid draw mode: " + tessellator.drawMode());
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
              .redLightMapUV(Utils.getRedPair(brightness))
              .greenLightMapUV(Utils.getGreenPair(brightness))
              .blueLightMapUV(Utils.getBluePair(brightness))
              .entityData(Shaders.getEntityData())
              .entityData2(Shaders.getEntityData2());

        tessellator.incrementAddedVertices();
        tessellator.incrementVertexCount();
    }

    private void addTrianglePrimitive() {
        calculateTriangleNormal();
        calculateTangent();
        calculateTriangleMidTextureUV();

        addVertex(vertexA);
        addVertex(vertexB);
        addVertex(vertexC);
    }

    private void addQuadPrimitive() {
        calculateQuadNormal();
        calculateTangent();
        calculateQuadMidTextureUV();

        addVertex(vertexA);
        addVertex(vertexB);
        addVertex(vertexC);
        addVertex(vertexD);
    }

    private void calculateTriangleNormal() {
        val x1 = vertexB.positionX() - vertexA.positionX();
        val y1 = vertexB.positionY() - vertexA.positionY();
        val z1 = vertexB.positionZ() - vertexA.positionZ();
        val x2 = vertexC.positionX() - vertexB.positionX();
        val y2 = vertexC.positionY() - vertexB.positionY();
        val z2 = vertexC.positionZ() - vertexB.positionZ();

        var vnx = y1 * z2 - y2 * z1;
        var vny = z1 * x2 - z2 * x1;
        var vnz = x1 * y2 - x2 * y1;

        var lensq = vnx * vnx + vny * vny + vnz * vnz;
        var mult = lensq != 0.0 ? (float) (1F / Math.sqrt(lensq)) : 1.0F;
        vnx *= mult;
        vny *= mult;
        vnz *= mult;

        vertexA.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);
        vertexB.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);
        vertexC.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);

        tessellator.hasNormals(true);
    }

    private void calculateQuadNormal() {
        val x1 = vertexC.positionX() - vertexA.positionX();
        val y1 = vertexC.positionY() - vertexA.positionY();
        val z1 = vertexC.positionZ() - vertexA.positionZ();
        val x2 = vertexD.positionX() - vertexB.positionX();
        val y2 = vertexD.positionY() - vertexB.positionY();
        val z2 = vertexD.positionZ() - vertexB.positionZ();

        var vnx = y1 * z2 - y2 * z1;
        var vny = z1 * x2 - z2 * x1;
        var vnz = x1 * y2 - x2 * y1;
        var lensq = vnx * vnx + vny * vny + vnz * vnz;
        var mult = (double) lensq != 0F ? (float) (1F / Math.sqrt(lensq)) : 1F;
        vnx *= mult;
        vny *= mult;
        vnz *= mult;

        vertexA.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);
        vertexB.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);
        vertexC.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);
        vertexD.normalX(vnx)
               .normalY(vny)
               .normalZ(vnz);

        tessellator.hasNormals(true);
    }

    private void calculateTangent() {
        val x1 = vertexB.positionX() - vertexA.positionX();
        val y1 = vertexB.positionY() - vertexA.positionY();
        val z1 = vertexB.positionZ() - vertexA.positionZ();
        val u1 = vertexB.textureU() - vertexA.textureU();
        val v1 = vertexB.textureV() - vertexA.textureV();

        val x2 = vertexC.positionX() - vertexA.positionX();
        val y2 = vertexC.positionY() - vertexA.positionY();
        val z2 = vertexC.positionZ() - vertexA.positionZ();
        val u2 = vertexC.textureU() - vertexA.textureU();
        val v2 = vertexC.textureV() - vertexA.textureV();

        val d = u1 * v2 - u2 * v1;
        val r = d != 0F ? 1F / d : 1F;

        var tan1x = (v2 * x1 - v1 * x2) * r;
        var tan1y = (v2 * y1 - v1 * y2) * r;
        var tan1z = (v2 * z1 - v1 * z2) * r;
        var lensq = tan1x * tan1x + tan1y * tan1y + tan1z * tan1z;
        var mult = lensq != 0F ? (float) (1F / Math.sqrt(lensq)) : 1F;
        tan1x *= mult;
        tan1y *= mult;
        tan1z *= mult;

        var tan2x = (u1 * x2 - u2 * x1) * r;
        var tan2y = (u1 * y2 - u2 * y1) * r;
        var tan2z = (u1 * z2 - u2 * z1) * r;
        lensq = tan2x * tan2x + tan2y * tan2y + tan2z * tan2z;
        mult = lensq != 0F ? (float) (1F / Math.sqrt(lensq)) : 10F;
        tan2x *= mult;
        tan2y *= mult;
        tan2z *= mult;

        val tan3x = vertexA.normalX() * tan1y - vertexA.normalX() * tan1z;
        val tan3y = vertexA.normalY() * tan1z - vertexA.normalY() * tan1x;
        val tan3z = vertexA.normalZ() * tan1x - vertexA.normalZ() * tan1y;
        val tan1w = tan2x * tan3x + tan2y * tan3y + tan2z * tan3z < 0F ? -1F : 1F;

        if (tessellator.drawMode() == GL11.GL_TRIANGLES) {
            vertexA.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
            vertexB.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
            vertexC.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
        } else if (tessellator.drawMode() == GL11.GL_QUADS) {
            vertexA.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
            vertexB.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
            vertexC.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
            vertexD.tangentX(tan1x)
                   .tangentY(tan1y)
                   .tangentZ(tan1z)
                   .tangentW(tan1w);
        }
    }

    private void calculateTriangleMidTextureUV() {
        val midTextureU = vertexA.textureU() +
                          vertexB.textureU() +
                          vertexC.textureU() /
                          TRIANGLE_VERTEX_COUNT;
        vertexA.midTextureU(midTextureU);
        vertexB.midTextureU(midTextureU);
        vertexC.midTextureU(midTextureU);

        val midTextureV = vertexA.textureV() +
                          vertexB.textureV() +
                          vertexC.textureV() /
                          TRIANGLE_VERTEX_COUNT;
        vertexA.midTextureV(midTextureV);
        vertexB.midTextureV(midTextureV);
        vertexC.midTextureV(midTextureV);
    }

    private void calculateQuadMidTextureUV() {
        val midTextureU = vertexA.textureU() +
                          vertexB.textureU() +
                          vertexC.textureU() +
                          vertexD.textureU() /
                          QUAD_VERTEX_COUNT;
        vertexA.midTextureU(midTextureU);
        vertexB.midTextureU(midTextureU);
        vertexC.midTextureU(midTextureU);
        vertexD.midTextureU(midTextureU);

        val midTextureV = vertexA.textureV() +
                          vertexB.textureV() +
                          vertexC.textureV() +
                          vertexD.textureV() /
                          QUAD_VERTEX_COUNT;
        vertexA.midTextureV(midTextureV);
        vertexB.midTextureV(midTextureV);
        vertexC.midTextureV(midTextureV);
        vertexD.midTextureV(midTextureV);
    }

    private void addVertex(ShaderVertex vertex) {
        vertex.toIntArray(tessellator.rawBufferIndex(), tessellator.rawBuffer());

        tessellator.incrementRawBufferIndex(VERTEX_STRIDE_INTS);
    }
}
