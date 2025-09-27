package com.github.debris.debrisclient.localization;

public enum GameLogText implements Translatable {
    PORTAL_CREATED,
    THUNDER_START,
    THUNDER_END,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("game_log").resolve(this).build();
    }
}
