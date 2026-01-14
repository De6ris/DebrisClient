package com.github.debris.debrisclient.localization;

public enum GeneralText implements Translatable {
    CLICK_HERE,
    CLICK_TO_OPEN,
    CLICK_TO_EXECUTE,
    HERE,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve(this).build();
    }
}
