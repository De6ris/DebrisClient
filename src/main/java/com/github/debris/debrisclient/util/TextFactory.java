package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.localization.GeneralText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextFactory {
    public static final Component ON = Component.empty().append(CommonComponents.OPTION_ON).withStyle(style -> style.withColor(ChatFormatting.GREEN));
    public static final Component OFF = Component.empty().append(CommonComponents.OPTION_OFF).withStyle(style -> style.withColor(ChatFormatting.RED));
    public static final Component DEBUG = Component.translatable("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);
    private static final Component CLICK_HERE = GeneralText.CLICK_HERE.translate().withStyle(ChatFormatting.AQUA);
    private static final Component HERE = GeneralText.HERE.translate().withStyle(ChatFormatting.AQUA);

    public static Component onOrOff(boolean on) {
        return on ? ON : OFF;
    }

    public static MutableComponent listEntry(String entry) {
        return listEntry(Component.literal(entry));
    }

    public static MutableComponent listEntry(Component entry) {
        return Component.empty().append("- ").append(entry).append(Component.literal(": "));
    }

    public static MutableComponent clickHere() {
        return Component.empty().append(CLICK_HERE);
    }

    public static MutableComponent here() {
        return Component.empty().append(HERE);
    }
}
