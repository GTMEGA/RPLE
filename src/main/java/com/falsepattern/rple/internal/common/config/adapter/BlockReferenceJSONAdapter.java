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

package com.falsepattern.rple.internal.common.config.adapter;

import com.falsepattern.rple.internal.common.config.container.BlockReference;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NoArgsConstructor;

import static com.falsepattern.rple.internal.common.config.ColorConfigLoader.colorConfigGSON;

@NoArgsConstructor
public final class BlockReferenceJSONAdapter extends TypeAdapter<BlockReference> {
    @Override
    public void write(JsonWriter out, BlockReference value) {
        colorConfigGSON().toJson(value.toString(), String.class, out);
    }

    @Override
    public BlockReference read(JsonReader in) {
        return new BlockReference(colorConfigGSON().<String>fromJson(in, String.class));
    }
}
