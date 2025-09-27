package com.github.debris.debrisclient.util;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TextFactory {
    public static final Text ON = Text.empty().append(ScreenTexts.ON).styled(style -> style.withColor(Formatting.GREEN));
    public static final Text OFF = Text.empty().append(ScreenTexts.OFF).styled(style -> style.withColor(Formatting.RED));
    public static final Text DEBUG = Text.translatable("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD);

    public static Text onOrOff(boolean on) {
        return on ? ON : OFF;
    }

    public static MutableText listEntry(String entry) {
        return listEntry(Text.literal(entry));
    }

    public static MutableText listEntry(Text entry) {
        return Text.empty().append("- ").append(entry).append(Text.literal(": "));
    }
}
