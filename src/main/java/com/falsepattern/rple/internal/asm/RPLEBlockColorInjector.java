package com.falsepattern.rple.internal.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import static com.falsepattern.rple.internal.RightProperLightingEngine.createLogger;

public final class RPLEBlockColorInjector implements IClassTransformer {
    private static final Logger LOG = createLogger("RPLE Block Color Injector");

    @Override
    public byte @Nullable [] transform(String name, String transformedName, byte @Nullable [] classBytes) {
        if (classBytes == null || name.startsWith("com.falsepattern.rple"))
            return classBytes;
        return classBytes;
    }
}
