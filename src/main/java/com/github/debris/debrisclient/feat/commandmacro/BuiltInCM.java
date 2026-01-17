package com.github.debris.debrisclient.feat.commandmacro;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum BuiltInCM {
    SPAWN("/player bot_${code} spawn at ${pos}"),
    KILL("/player bot_${code} kill"),
    ATTACK("/player bot_${code} attack"),
    USE("/player bot_${code} use")
    ;

    private final String command;

    public static final List<BuiltInCM> VALUES = ImmutableList.copyOf(values());

    BuiltInCM(String command) {
        this.command = command;
    }

    public BuiltInCM next() {
        return VALUES.get((this.ordinal() + 1) % VALUES.size());
    }

    public String getCommand() {
        return command;
    }
}
