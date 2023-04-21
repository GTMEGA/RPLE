package com.falsepattern.rple.internal.mixin.mixins.common.lumina;

import com.falsepattern.rple.internal.storage.ColoredCarrierChunk;
import com.falsepattern.rple.internal.storage.ColoredCarrierWorld;
import com.falsepattern.rple.internal.storage.ColoredLightChannel;
import com.falsepattern.rple.internal.storage.ColoredLightChunk;
import com.falsepattern.rple.internal.storage.ColoredLightWorld;
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
    public ColoredLightChunk getColoredChunk(ColoredLightChannel channel) {
        if (!colorInit) {
            initColoredChunk();
            colorInit = true;
        }
        switch (channel) {
            case RED: return cRed;
            case GREEN: return cGreen;
            case BLUE: return cBlue;
            default: throw new IllegalArgumentException();
        }
    }


    private void initColoredChunk() {
        val carr = (ColoredCarrierWorld)worldObj;
        cRed = new ColoredLightChunk(carr.getColoredWorld(ColoredLightChannel.RED), (Chunk) (Object) this);
        cGreen = new ColoredLightChunk(carr.getColoredWorld(ColoredLightChannel.GREEN), (Chunk) (Object) this);
        cBlue = new ColoredLightChunk(carr.getColoredWorld(ColoredLightChannel.BLUE), (Chunk) (Object) this);
    }
}
