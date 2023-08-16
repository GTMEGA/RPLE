/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.common.block;

import com.falsepattern.rple.internal.common.util.ClassBlockList;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.val;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;

public class TransparencyRecursionDoctor {
    private static final ClassBlockList BLOCK_LIST_RAW = new ClassBlockList("TransparencyRecursionDoctor",
                                                                            "recursion",
                                                                            Block.class,
                                                                            (klass) -> hasMethod(Variant.Raw, klass));
    private static final ClassBlockList BLOCK_LIST_POSITIONAL = new ClassBlockList("TransparencyRecursionDoctor",
                                                                                   "recursion",
                                                                                   Block.class,
                                                                                   (klass) -> hasMethod(Variant.Positional, klass));
    @AllArgsConstructor
    public enum Variant {
        Raw(new String[]{"func_149717_k", "getLightOpacity"}, new Class[0]),
        Positional(new String[]{"getLightOpacity"}, new Class[]{IBlockAccess.class, int.class, int.class, int.class});
        final String[] names;
        final Class<?>[] args;

        ClassBlockList theList() {
            if (this == Variant.Positional) {
                return BLOCK_LIST_POSITIONAL;
            }
            return BLOCK_LIST_RAW;
        }
    }

    public static void permit(Variant variant, Class<? extends Entity> klass) {
        variant.theList().permit(klass);
    }

    public static void permit(Variant variant, String klass) {
        variant.theList().permit(klass);
    }

    public static boolean isOnBlockList(Variant variant, Class<?> klass) {
        return variant.theList().isOnBlockList(klass);
    }

    private static boolean hasMethod(Variant variant, Class<?> klass) {
        boolean found = false;
        for (val name : variant.names) {
            try {
                klass.getDeclaredMethod(name, variant.args);
                found = true;
                break;
            } catch (Throwable ignored) {
            }
        }
        return found;
    }
}
