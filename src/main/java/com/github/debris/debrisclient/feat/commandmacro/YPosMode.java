package com.github.debris.debrisclient.feat.commandmacro;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;

public enum YPosMode {
    FIXED_VALUE,
    SURFACE,
    ;
    private static final ImmutableList<YPosMode> VALUES = ImmutableList.copyOf(values());

    public YPosMode next() {
        return VALUES.get((this.ordinal() + 1) % VALUES.size());
    }

    public Component label() {
        return switch (this) {
            case FIXED_VALUE -> Component.literal("固定值");
            case SURFACE -> Component.literal("地表");
        };
    }
}
