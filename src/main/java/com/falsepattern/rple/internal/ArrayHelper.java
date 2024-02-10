package com.falsepattern.rple.internal;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ArrayHelper {
    @Contract(pure = true)
    public static boolean isZero(byte @NotNull [] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0)
                return false;
        }
        return true;
    }
}
