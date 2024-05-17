/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 *
 */

package com.falsepattern.rple.internal.asm;

import com.falsepattern.lib.asm.IClassNodeTransformer;
import com.falsepattern.lib.asm.SmartTransformer;
import com.falsepattern.rple.internal.common.config.RPLEConfig;
import com.falsepattern.rple.internal.common.util.FastThreadLocal;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;

@Accessors(fluent = true, chain = false)
public final class RPLETransformer implements SmartTransformer {
    static final Logger LOG = createLogger("ASM");

    static {
        RPLEConfig.poke();
        FastThreadLocal.setMainThread(Thread.currentThread());
    }

    @Getter
    private final List<IClassNodeTransformer> transformers;

    @Getter
    private final Logger logger = LOG;

    public RPLETransformer() {
        transformers = Arrays.asList(new RPLEBlockColorInjector());
    }
}
