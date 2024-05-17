package com.falsepattern.rple.internal.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FastThreadLocal<S> {
    private static Thread MAIN_THREAD = null;

    private final ThreadLocal<S> threadLocal;
    private S mainThreadValue;

    public static void setMainThread(Thread mainThread) {
        MAIN_THREAD = mainThread;
    }

    public static <S> FastThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
        if (MAIN_THREAD == null)
            throw new IllegalStateException("Main Thread not set (Initialization failed?)");

        return new FastThreadLocal<>(ThreadLocal.withInitial(supplier), supplier.get());
    }

    public void set(@Nullable S value) {
        if (isMainThread()) {
            mainThreadValue = value;
        } else {
            threadLocal.set(value);
        }
    }

    public @Nullable S get() {
        return isMainThread() ? mainThreadValue : threadLocal.get();
    }

    private boolean isMainThread() {
        return Thread.currentThread() == MAIN_THREAD;
    }
}
