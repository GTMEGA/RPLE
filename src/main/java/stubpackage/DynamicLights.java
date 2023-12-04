/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
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
