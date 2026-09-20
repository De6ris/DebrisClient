package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CMGeneratorScreen extends CottonClientScreen {
    public static final CMGeneratorScreen INSTANCE = new CMGeneratorScreen();

    public CMGeneratorScreen() {
        super(Component.literal("指令宏生成器").withStyle(ChatFormatting.LIGHT_PURPLE), new CMGeneratorDescription());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
