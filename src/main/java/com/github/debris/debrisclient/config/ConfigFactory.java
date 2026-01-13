package com.github.debris.debrisclient.config;

import com.github.debris.debrisclient.config.options.ConfigEnum;
import com.github.debris.debrisclient.config.options.ConfigHotKeyExtend;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class ConfigFactory {
    private static final String DEFAULT_COMMENT = "no comment";

    public static ConfigBoolean ofBoolean(String name, boolean defaultValue) {
        return ofBoolean(name, defaultValue, DEFAULT_COMMENT);
    }

    public static ConfigBoolean ofBoolean(String name, boolean defaultValue, String comment) {
        return new ConfigBoolean(name, defaultValue, comment);
    }

    public static ConfigInteger ofInteger(String name, int defaultValue, int minValue, int maxValue) {
        return ofInteger(name, defaultValue, minValue, maxValue, false);
    }

    public static ConfigInteger ofInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        return ofInteger(name, defaultValue, minValue, maxValue, useSlider, DEFAULT_COMMENT);
    }

    public static ConfigInteger ofInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider, String comment) {
        return new ConfigInteger(name, defaultValue, minValue, maxValue, useSlider, comment);
    }

    public static ConfigColor ofColor(String name, String defaultValue, String comment) {
        return new ConfigColor(name, defaultValue, comment);
    }

    public static ConfigString ofString(String name, String defaultValue) {
        return new ConfigString(name, defaultValue);
    }

    public static ConfigStringList ofStringList(String name) {
        return ofStringList(name, ImmutableList.of());
    }

    public static ConfigStringList ofStringList(String name, ImmutableList<String> defaultValue) {
        return ofStringList(name, defaultValue, DEFAULT_COMMENT);
    }

    public static ConfigStringList ofStringList(String name, ImmutableList<String> defaultValue, String comment) {
        return new ConfigStringList(name, defaultValue, comment);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString) {
        return ofHotkey(name, defaultStorageString, DEFAULT_COMMENT);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, String comment) {
        return ofHotkey(name, defaultStorageString, KeybindSettings.DEFAULT, comment);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, KeybindSettings settings) {
        return ofHotkey(name, defaultStorageString, settings, DEFAULT_COMMENT);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment) {
        return new ConfigHotKeyExtend(name, defaultStorageString, settings, comment);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey) {
        return ofBooleanHotkeyed(name, defaultValue, defaultHotkey, DEFAULT_COMMENT);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment) {
        return ofBooleanHotkeyed(name, defaultValue, defaultHotkey, KeybindSettings.DEFAULT, comment);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings) {
        return ofBooleanHotkeyed(name, defaultValue, defaultHotkey, settings, DEFAULT_COMMENT);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment) {
        return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey, settings, comment);
    }

    public static <T extends Enum<T>> ConfigEnum<T> ofEnum(String name, T defaultValue) {
        return ofEnum(name, defaultValue, DEFAULT_COMMENT);
    }

    public static <T extends Enum<T>> ConfigEnum<T> ofEnum(String name, T defaultValue, String comment) {
        return new ConfigEnum<>(name, defaultValue, comment);
    }
}
