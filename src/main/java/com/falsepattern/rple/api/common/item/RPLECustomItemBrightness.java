/*
 * Right Proper Lighting Engine
 *
 * Copyright (C) 2023-2025 FalsePattern, Ven
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
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

package com.falsepattern.rple.api.common.item;

import net.minecraft.item.ItemStack;

/**
 * This interface can be implemented onto any {@link net.minecraft.item.Item} to provide colored brightness for its dynamic lights.
 *
 * @since 1.6.0
 * @see RPLEItemColorRegistry
 */
public interface RPLECustomItemBrightness {
    /**
     * @return 0xrgb. -1 to defer to handlers instead. Handled by the default RPLE color handler (priority 0).
     */
    short rple$getCustomBrightnessColor(ItemStack stack);
}
