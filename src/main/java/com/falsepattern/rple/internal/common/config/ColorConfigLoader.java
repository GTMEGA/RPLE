/*
 * Copyright (c) 2023 FalsePattern, Ven
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */

package com.falsepattern.rple.internal.common.config;

import com.falsepattern.lib.util.FileUtil;
import com.falsepattern.rple.internal.Tags;
import com.falsepattern.rple.internal.common.config.adapter.BlockReferenceJSONAdapter;
import com.falsepattern.rple.internal.common.config.adapter.ColorReferenceJSONAdapter;
import com.falsepattern.rple.internal.common.config.adapter.HexColorJSONAdapter;
import com.falsepattern.rple.internal.common.config.adapter.PaletteJSONAdapter;
import com.falsepattern.rple.internal.common.config.container.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.falsepattern.rple.internal.common.util.LogHelper.createLogger;

@UtilityClass
public final class ColorConfigLoader {
    private static final Logger LOG = createLogger("ColorConfigLoader");

    private static final Gson GSON;

    private static final Path CONFIG_PATH;
    private static final File CONFIG_README_FILE;
    private static final File GENERATED_CONFIG_FILE;
    private static final File CUSTOM_CONFIG_FILE;

    private static final String README_TEXT = "Copy the `generated_colors.json` to `custom_colors.json`.\n" +
                                              "`generated_colors.json` is never read, only written to on startup.\n " +
                                              "If `custom_colors.json` exists, it will be used to set the colors of any block configured in it.\n" +
                                              "`custom_colors.json` does not act as an overlay, it acts as a definition.\n" +
                                              "If `custom_colors.json` does not have a block, it will not be configured.\n" +
                                              "Some mods may be implementing their own color values, and as such configurations may be provided by their authors.";

    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(ColorPalette.class, new PaletteJSONAdapter())
                .registerTypeAdapter(HexColor.class, new HexColorJSONAdapter())
                .registerTypeAdapter(BlockReference.class, new BlockReferenceJSONAdapter())
                .registerTypeAdapter(ColorReference.class, new ColorReferenceJSONAdapter())
                .create();

        CONFIG_PATH = FileUtil.getMinecraftHome()
                              .toPath()
                              .resolve("config")
                              .resolve(Tags.MOD_ID);
        CONFIG_README_FILE = CONFIG_PATH.resolve("README.txt").toFile();
        GENERATED_CONFIG_FILE = CONFIG_PATH.resolve("generated_colors.json").toFile();
        CUSTOM_CONFIG_FILE = CONFIG_PATH.resolve("custom_colors.json").toFile();
    }

    public static Gson colorConfigGSON() {
        return GSON;
    }

    public static void generateReadmeFile() {
        try {
            Files.createDirectories(CONFIG_PATH);
            FileUtils.writeStringToFile(CONFIG_README_FILE, README_TEXT);
        } catch (IOException e) {
            LOG.error("Failed to generate README.txt", e);
        }
    }

    public static void saveGeneratedConfig(BlockColorConfig config) throws IOException {
        val json = fromObjectToPrettyPrintJson(config);

        Files.createDirectories(CONFIG_PATH);
        FileUtils.writeStringToFile(GENERATED_CONFIG_FILE, json);
    }

    public static Optional<BlockColorConfig> loadCustomConfig() throws IOException {
        if (!CUSTOM_CONFIG_FILE.isFile())
            return Optional.empty();

        val json = FileUtils.readFileToString(CUSTOM_CONFIG_FILE);
        try {
            val config = GSON.fromJson(json, BlockColorConfig.class);
            return Optional.of(config);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    public static String fromObjectToPrettyPrintJson(Object source) {
        val stringWriter = new StringWriter();
        val jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setIndent("    ");

        GSON.toJson(source, source.getClass(), jsonWriter);
        return stringWriter.toString();
    }

    public static void logParsingError(String message, Object... params) {
        LOG.warn(message, params);
    }
}
