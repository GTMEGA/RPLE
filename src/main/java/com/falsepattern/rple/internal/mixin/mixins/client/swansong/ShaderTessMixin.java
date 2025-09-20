package com.falsepattern.rple.internal.mixin.mixins.client.swansong;

import com.falsepattern.rple.api.client.ClientColorHelper;
import com.falsepattern.rple.internal.client.lightmap.LightMap;
import com.falsepattern.rple.internal.mixin.interfaces.ITessellatorMixin;
import com.falsepattern.rple.internal.mixin.interfaces.swansong.RPLEShaderVert;
import com.ventooth.swansong.tessellator.ShaderTess;
import com.ventooth.swansong.tessellator.ShaderVert;
import lombok.val;
import net.minecraft.client.renderer.Tessellator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ShaderTess.class, remap = false)
public abstract class ShaderTessMixin {
    @Shadow
    @Final
    protected Tessellator tess;

    @Redirect(method = "draw",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/client/renderer/Tessellator;hasBrightness:Z",
                       opcode = Opcodes.GETFIELD,
                       remap = true,
                       ordinal = 0),
              require = 1)
    private boolean enableLightMaps(Tessellator tess) {
        if (tess.hasBrightness) {
            val rpleTess = ITessellatorMixin.of(tess);
            val shortBuffer = rpleTess.rple$shortBuffer();
            LightMap.lightMap().enable();
            LightMap.lightMap().enableVertexPointers(shortBuffer);
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
    private boolean disableLightMaps(Tessellator tess) {
        if (tess.hasBrightness) {
            LightMap.lightMap().disableVertexPointers();
        }
        return false;
    }

    @Redirect(method = "prepareVertex",
              at = @At(value = "FIELD",
                       target = "Lcom/ventooth/swansong/tessellator/ShaderVert;lightMapUV:I",
                       opcode = Opcodes.PUTFIELD),
              require = 1)
    private void setOurFunColorsYayyy(ShaderVert instance, int value) {
        val rpleVert = RPLEShaderVert.of(instance);
        val rpleTess = ITessellatorMixin.of(tess);
        val rgb64 = rpleTess.rple$getRGB64Brightness();

        rpleVert.rple$lightMapUVRed(ClientColorHelper.tessFromRGB64Red(rgb64));
        rpleVert.rple$lightMapUVGreen(ClientColorHelper.tessFromRGB64Green(rgb64));
        rpleVert.rple$lightMapUVBlue(ClientColorHelper.tessFromRGB64Blue(rgb64));
    }
}
