/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2024 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * This program comes with additional permissions according to Section 7 of the
 * GNU Affero General Public License. See the full LICENSE file for details.
 */

package com.falsepattern.rple.internal.common.cache;

import com.falsepattern.lumi.api.lighting.LightType;
import com.falsepattern.lumi.api.storage.LumiBlockStorage;
import com.falsepattern.rple.api.common.color.ColorChannel;
import com.falsepattern.rple.internal.common.chunk.RPLEChunk;
import com.falsepattern.rple.internal.common.world.RPLEWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RPLEBlockStorage extends LumiBlockStorage {
    @NotNull ColorChannel rple$channel();

    @Override
    @NotNull RPLEBlockStorageRoot lumi$root();

    @Override
    @NotNull RPLEWorld lumi$world();

    default int rple$getChannelBrightnessForTessellator(int posX, int posY, int posZ, int minBlockLight) {
        return rple$getChannelBrightnessForTessellator(lumi$getChunkFromBlockPosIfExists(posX, posZ), posX, posY, posZ, minBlockLight);
    }

    int rple$getChannelBrightnessForTessellator(@Nullable RPLEChunk chunk, int posX, int posY, int posZ, int minBlockLight);

    default int rple$getChannelLightValueForRender(@NotNull LightType lightType, int posX, int posY, int posZ) {
        return rple$getChannelLightValueForRender(lumi$getChunkFromBlockPosIfExists(posX, posZ), lightType, posX, posY, posZ);
    }

    int rple$getChannelLightValueForRender(@Nullable RPLEChunk chunk, @NotNull LightType lightType, int posX, int posY, int posZ);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromBlockPosIfExists(int posX, int posZ);

    @Override
    @Nullable RPLEChunk lumi$getChunkFromChunkPosIfExists(int chunkPosX, int chunkPosZ);
}
