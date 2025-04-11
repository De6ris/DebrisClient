package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.config.options.ConfigEnum;
import com.github.Debris.DebrisClient.config.options.ConfigHotKeyExtend;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class ConfigFactory {
    public static ConfigBoolean ofBoolean(String name, boolean defaultValue) {
        return new ConfigBoolean(name, defaultValue);
    }

    public static ConfigBoolean ofBoolean(String name, boolean defaultValue, String comment) {
        return new ConfigBoolean(name, defaultValue, comment);
    }

    public static ConfigInteger ofInteger(String name, int defaultValue, int minValue, int maxValue) {
        return new ConfigInteger(name, defaultValue, minValue, maxValue);
    }

    public static ConfigInteger ofInteger(String name, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        return new ConfigInteger(name, defaultValue, minValue, maxValue, useSlider);
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
        return new ConfigStringList(name, defaultValue);
    }

    public static ConfigStringList ofStringList(String name, ImmutableList<String> defaultValue, String comment) {
        return new ConfigStringList(name, defaultValue, comment);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString) {
        return new ConfigHotKeyExtend(name, defaultStorageString);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, String comment) {
        return new ConfigHotKeyExtend(name, defaultStorageString, comment);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, KeybindSettings settings) {
        return new ConfigHotKeyExtend(name, defaultStorageString, settings);
    }

    public static ConfigHotkey ofHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment) {
        return new ConfigHotKeyExtend(name, defaultStorageString, settings, comment);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey) {
        return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, String comment) {
        return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey, comment);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings) {
        return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey, settings);
    }

    public static ConfigBooleanHotkeyed ofBooleanHotkeyed(String name, boolean defaultValue, String defaultHotkey, KeybindSettings settings, String comment) {
        return new ConfigBooleanHotkeyed(name, defaultValue, defaultHotkey, settings, comment);
    }

    public static <T extends Enum<T>> ConfigEnum<T> ofEnum(String name, T defaultValue) {
        return new ConfigEnum<>(name, defaultValue, "");
    }

    public static <T extends Enum<T>> ConfigEnum<T> ofEnum(String name, T defaultValue, String comment) {
        return new ConfigEnum<>(name, defaultValue, comment);
    }
}
