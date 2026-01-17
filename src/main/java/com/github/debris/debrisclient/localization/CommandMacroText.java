package com.github.debris.debrisclient.localization;

public enum CommandMacroText implements Translatable {
    EXAMPLE_CREATED,
    FILE_NOT_FOUND,
    HELP,
    ILLEGAL_PERIOD,
    NOT_JSON,
    READ_FILE_ERROR,
    STOPPED,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("command_macro_command").resolve(this).build();
    }
}
