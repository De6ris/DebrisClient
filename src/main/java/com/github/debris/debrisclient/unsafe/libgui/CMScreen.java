package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class CMScreen extends CottonClientScreen {
    public static final CMScreen INSTANCE = new CMScreen();

    public CMScreen() {
        super(Component.literal("假人指令宏生成器").withStyle(ChatFormatting.LIGHT_PURPLE), new CMGuiDescription());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
