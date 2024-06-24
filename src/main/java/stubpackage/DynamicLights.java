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

package stubpackage;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;

public class DynamicLights {

    public static void entityAdded(Entity entityIn, RenderGlobal renderGlobal) {

    }

    public static void entityRemoved(Entity entityIn, RenderGlobal renderGlobal) {

    }

    public static void update(RenderGlobal renderGlobal) {

    }

    public static int getCombinedLight(int x, int y, int z, int combinedLight) {
        return 0;
    }

    public static int getCombinedLight(Entity entity, int combinedLight) {
        return 0;
    }

    public static void removeLights(RenderGlobal renderGlobal) {

    }

    public static void clear() {

    }

    public static int getCount() {
        return 0;
    }
}
