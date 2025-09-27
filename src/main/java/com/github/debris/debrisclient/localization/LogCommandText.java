package com.github.debris.debrisclient.localization;

public enum LogCommandText implements Translatable {
    CATEGORY_NOT_EXIST,
    CLICK_TO_TOGGLE,
    SET_OPTION,
    SUBSCRIBE,
    UNSUBSCRIBE,
    UNSUBSCRIBE_ALL,
    ;


    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("log_command").resolve(this).build();
    }
}
