package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CMTitleScreen extends CottonClientScreen {
    public static final CMTitleScreen INSTANCE = new CMTitleScreen();

    public CMTitleScreen() {
        super(Component.literal("指令宏").withStyle(ChatFormatting.LIGHT_PURPLE), new CMTitleDescription());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
