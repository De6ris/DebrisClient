package com.github.debris.debrisclient.localization;

public enum CommandMacroText implements Translatable {
    EXAMPLE_CREATED,
    HELP,
    ILLEGAL_PERIOD,
    STOPPED,
    ;

    @Override
    public String getTranslationKey() {
        return Translatable.root().resolve("command_macro_command").resolve(this).build();
    }
}
