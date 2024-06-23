package com.falsepattern.rple.api.client;

import com.falsepattern.lib.StableAPI;
import com.falsepattern.rple.internal.client.render.EntityColorHandler;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

@StableAPI(since = "1.0.0")
public final class RPLEEntityAPI {
    private RPLEEntityAPI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @StableAPI.Expose
    public static void permit(@NotNull Class<? extends Entity> entityClass) {
        EntityColorHandler.permit(entityClass);
    }

    /**
     * By default, any entity that overrides getBrightnessForRender will get a vanilla-style light value from Entity.getBrightnessForRender.
     * You can use this to remove this blocking logic from specific classes. Note: make sure all your superclasses
     * also behave correctly with colored lights!
     *
     * @param entityClassName The fully qualified class name
     */
    @StableAPI.Expose
    public static void permit(@NotNull String entityClassName) {
        EntityColorHandler.permit(entityClassName);
    }
}
