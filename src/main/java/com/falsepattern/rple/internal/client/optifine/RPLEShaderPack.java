package com.falsepattern.rple.internal.client.optifine;

import shadersmod.client.ShaderPackDefault;

import java.io.InputStream;

public class RPLEShaderPack extends ShaderPackDefault {
    public static final String NAME = "(RPLE example shader for testing)";
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public InputStream getResourceAsStream(String var1) {
        return RPLEShaderPack.class.getResourceAsStream(var1);
    }
}
