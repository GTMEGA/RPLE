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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import net.minecraft.item.ItemStack;

/**
 * API for adding custom lights to items (held by mobs, players, in item frames, and dropped on ground).
 * @since 1.6.0
 * @see com.falsepattern.rple.api.common.event.ItemColorRegistrationEvent
 */
@ApiStatus.NonExtendable
public interface RPLEItemColorRegistry {
    /**
     * Adds a custom colorizer callback. Use this if you don't want to add {@link RPLECustomItemBrightness} to an item class.
     * @param callback The colorizer. Return -1 if this colorizer does not want to colorize the provided item stack.
     * @param sortingIndex The numeric sorting index of the callback, used for ordering multiple colorizers. The default RPLE
     *                     colorizer is index 0. Indices can overlap, in which case the relative ordering of identical index
     *                     colorizers is undefined.
     */
    void registerItemColorCallback(ItemColorizerFunction callback, int sortingIndex);
}
