/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.common.chunk.RPLEChunkRoot;
import lombok.val;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.chunk.Chunk;

import static com.falsepattern.rple.api.common.color.ColorChannel.BLUE_CHANNEL;
import static com.falsepattern.rple.api.common.color.ColorChannel.GREEN_CHANNEL;
import static com.falsepattern.rple.api.common.color.ColorChannel.RED_CHANNEL;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements RPLEChunkRoot {
    /**
     * @author FalsePattern
     * @reason Behaviour fix
     */
    @Overwrite
    public int getHeightValue(int x, int z) {
        val red = this.rple$chunk(RED_CHANNEL).lumi$skyLightHeight(x, z);
        val green = this.rple$chunk(GREEN_CHANNEL).lumi$skyLightHeight(x, z);
        val blue = this.rple$chunk(BLUE_CHANNEL).lumi$skyLightHeight(x, z);

        // Select whichever can reach the lowest height
        return Math.min(Math.min(red, green), blue);
    }

    /**
     * @author FalsePattern
     * @reason Behaviour fix
     */
    @Overwrite
    public boolean canBlockSeeTheSky(int x, int y, int z) {
        val red = this.rple$chunk(RED_CHANNEL).lumi$skyLightHeight(x, z);
        val green = this.rple$chunk(GREEN_CHANNEL).lumi$skyLightHeight(x, z);
        val blue = this.rple$chunk(BLUE_CHANNEL).lumi$skyLightHeight(x, z);

        return y >= red || y >= green || y >= blue;

    }
}
