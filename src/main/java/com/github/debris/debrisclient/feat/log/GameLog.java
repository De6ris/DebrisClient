package com.github.debris.debrisclient.feat.log;

import com.google.gson.JsonElement;

import java.util.Collection;

public interface GameLog {
    boolean isActive();

    void setActive(boolean active);

    default void toggle() {
        this.setActive(!this.isActive());
    }

    default boolean isInactive() {
        return !this.isActive();
    }

    void readJson(JsonElement jsonElement);

    JsonElement writeJson();

    Collection<String> getOptionNames();

    void setOption(String option, String value);

    boolean hasOption(String option);

    /**
     * Be careful parsing illegal values
     */
    String getOption(String option);
}
