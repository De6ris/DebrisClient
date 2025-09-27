package com.github.debris.debrisclient.localization;

public enum ListCommandText implements Translatable {
    ADD,
    CLEAR,
    DELETE,
    ENTRY_EXISTS,
    ENTRY_NOT_EXIST,
    FAIL_DEFAULT_ARGUMENT,
    HELP,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("list_command").resolve(this).build();
    }
}
