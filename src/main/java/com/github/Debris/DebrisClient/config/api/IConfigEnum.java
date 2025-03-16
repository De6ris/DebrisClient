package com.github.Debris.DebrisClient.config.api;

import com.github.Debris.DebrisClient.config.options.ConfigEnumEntryWrapper;

import java.util.List;

public interface IConfigEnum<T extends Enum<T>> {
    T getDefaultEnumValue();

    T getEnumValue();

    List<T> getAllEnumValues();

    List<ConfigEnumEntryWrapper<T>> getWrappers();
}
