package com.falsepattern.rple.internal.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import com.falsepattern.rple.internal.Tags;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.falsepattern.rple.internal.Tags.MOD_NAME;
import static cpw.mods.fml.relauncher.IFMLLoadingPlugin.*;

@Name(MOD_NAME + "|ASM Plugin")
@MCVersion("1.7.10")
@SortingIndex(Integer.MAX_VALUE)
@TransformerExclusions("com.falsepattern.lumina.internal.asm")
@NoArgsConstructor
public final class ASMLoadingPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{Tags.GROUP_NAME + ".internal.asm.RPLETransformer"};
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }
}
