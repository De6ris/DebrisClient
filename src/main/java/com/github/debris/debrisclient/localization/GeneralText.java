package com.github.debris.debrisclient.localization;

public enum GeneralText implements Translatable {
    CLICK_HERE,
    CLICK_TO_EXECUTE,
    CLICK_TO_OPEN,
    FEATURE_REQUIRES_MOD,
    FILE_NOT_FOUND,
    HERE,
    NOT_JSON,
    READ_FILE_ERROR,
    RELOAD_SUCCESS
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve(this).build();
    }
}
