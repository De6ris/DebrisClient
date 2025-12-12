package com.github.debris.debrisclient.localization;

public enum AdventureCommandText implements Translatable {
    HELP,
    NOT_AVAILABLE,
    SUPPORTED_MODS,
    STATUS_ON,
    STATUS_OFF,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("adventure_command").resolve(this).build();
    }
}
