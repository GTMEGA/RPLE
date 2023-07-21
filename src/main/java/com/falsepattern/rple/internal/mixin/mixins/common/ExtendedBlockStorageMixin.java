/*
 * Copyright (c) 2023 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.rple.internal.mixin.mixins.common;

import com.falsepattern.rple.internal.mixin.hook.ColoredLightingHooks;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ExtendedBlockStorage.class)
public abstract class ExtendedBlockStorageMixin {
    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite
    public int getExtSkylightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return ColoredLightingHooks.getMaxSkyLightValue(thiz(), subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    /**
     * @author Ven
     * @reason Colorize
     */
    @Overwrite
    public int getExtBlocklightValue(int subChunkPosX, int subChunkPosY, int subChunkPosZ) {
        return ColoredLightingHooks.getMaxBlockLightValue(thiz(), subChunkPosX, subChunkPosY, subChunkPosZ);
    }

    private ExtendedBlockStorage thiz() {
        return (ExtendedBlockStorage) (Object) this;
    }
}
