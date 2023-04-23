package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.api.LightConstants;
import com.falsepattern.rple.internal.storage.ColoredCarrierChunk;
import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChunk;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements ColoredCarrierChunk {
    @Shadow public World worldObj;
    private ColoredLightChunk cRed;
    private ColoredLightChunk cGreen;
    private ColoredLightChunk cBlue;
    private boolean colorInit;
    @Override
    public ColoredLightChunk getColoredChunk(int colorChannel) {
        if (!colorInit) {
            initColoredChunk();
            colorInit = true;
        }
        switch (colorChannel) {
            case LightConstants.COLOR_CHANNEL_RED: return cRed;
            case LightConstants.COLOR_CHANNEL_GREEN: return cGreen;
            case LightConstants.COLOR_CHANNEL_BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }


    private void initColoredChunk() {
        val carr = (ColoredCarrierWorld)worldObj;
        cRed = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_RED), (Chunk) (Object) this);
        cGreen = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_GREEN), (Chunk) (Object) this);
        cBlue = new ColoredLightChunk(carr.getColoredWorld(LightConstants.COLOR_CHANNEL_BLUE), (Chunk) (Object) this);
    }
}
