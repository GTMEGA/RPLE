/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.api;

import lombok.SneakyThrows;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;


/**
 * This class is implicitly implemented on {@link Block} using a mixin. You can safely cast any class that extends Block
 * to this interface.
 * Note: Due to mixin breaking super calls with mixin-implemented interfaces on super classes,
 * you need to use {@link SuperCallHelper}.
 */
@Deprecated
public interface ColoredBlock {
    @SneakyThrows
    default int getColoredLightValue(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        return 0;
    }

    @SneakyThrows
    default int getColoredLightOpacity(IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
        return 0;
    }

    @SneakyThrows
    default void setColoredLightValue(int meta, int r, int g, int b) {
    }

    @SneakyThrows
    default void setColoredLightOpacity(int meta, int r, int g, int b) {
    }

    class SuperCallHelper {
        private static final MethodType getColoredLightValueM;
        private static final MethodType getColoredLightOpacityM;
        private static final MethodType setColoredLightValueM;
        private static final MethodType setColoredLightOpacityM;

        static {
            try {
                getColoredLightValueM = getMType(ColoredBlock.class.getDeclaredMethod("getColoredLightValue", IBlockAccess.class, int.class, int.class, int.class, int.class, int.class));
                getColoredLightOpacityM = getMType(ColoredBlock.class.getDeclaredMethod("getColoredLightOpacity", IBlockAccess.class, int.class, int.class, int.class, int.class, int.class));
                setColoredLightValueM = getMType(ColoredBlock.class.getDeclaredMethod("setColoredLightValue", int.class, int.class, int.class, int.class));
                setColoredLightOpacityM = getMType(ColoredBlock.class.getDeclaredMethod("setColoredLightOpacity", int.class, int.class, int.class, int.class));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private static MethodType getMType(Method method) {
            return MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        }

        @SneakyThrows
        public static int getColoredLightValue(MethodHandles.Lookup lookup, Class<?> relativeTo, Object instance, IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
            return (int) doSuperCall(lookup, relativeTo, "getColoredLightValue", getColoredLightValueM).invoke(instance, world, meta, colorChannel, x, y, z);
        }

        @SneakyThrows
        public static int getColoredLightOpacity(MethodHandles.Lookup lookup, Class<?> relativeTo, Object instance, IBlockAccess world, int meta, int colorChannel, int x, int y, int z) {
            return (int) doSuperCall(lookup, relativeTo, "getColoredLightOpacity", getColoredLightOpacityM).invoke(instance, world, meta, colorChannel, x, y, z);
        }

        @SneakyThrows
        public static void setColoredLightValue(MethodHandles.Lookup lookup, Class<?> relativeTo, Object instance, int meta, int r, int g, int b) {
            doSuperCall(lookup, relativeTo, "setColoredLightValue", setColoredLightValueM).invoke(instance, meta, r, g, b);
        }

        @SneakyThrows
        public static void setColoredLightOpacity(MethodHandles.Lookup lookup, Class<?> relativeTo, Object instance, int meta, int r, int g, int b) {
            doSuperCall(lookup, relativeTo, "setColoredLightOpacity", setColoredLightOpacityM).invoke(instance, meta, r, g, b);
        }

        // TODO: [PRE_RELEASE] Dynamically finding/invoking MethodHandles is both more complicated -and- slower vs reflection
        // TODO: [PRE_RELEASE] Why is this in here again? Is this dead code, or does it belong elsewhere?
        private static MethodHandle doSuperCall(MethodHandles.Lookup lookup, Class<?> relativeTo, String name, MethodType type) {
            var klass = relativeTo.getSuperclass();
            while (klass != null) {
                try {
                    var handle = lookup.findSpecial(klass, name, type, relativeTo);
                    return handle;
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    e.printStackTrace();
                } catch (
                        Throwable e) {
                    throw new RuntimeException(e);
                }
                klass = klass.getSuperclass();
            }
            throw new RuntimeException("Could not super-call " + name + " with handle " + type);
        }
    }
}
