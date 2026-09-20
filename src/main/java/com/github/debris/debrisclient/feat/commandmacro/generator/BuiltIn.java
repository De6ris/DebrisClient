package com.github.debris.debrisclient.feat.commandmacro.generator;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum BuiltIn {
    SPAWN("/player bot_${code} spawn at ${pos}"),
    KILL("/player bot_${code} kill"),
    ATTACK("/player bot_${code} attack"),
    USE("/player bot_${code} use"),
    LOOK("/player bot_${code} look"),
    ;

    private final String command;

    public static final List<BuiltIn> VALUES = ImmutableList.copyOf(values());

    BuiltIn(String command) {
        this.command = command;
    }

    public BuiltIn next() {
        return VALUES.get((this.ordinal() + 1) % VALUES.size());
    }

    public String getCommand() {
        return command;
    }
}
