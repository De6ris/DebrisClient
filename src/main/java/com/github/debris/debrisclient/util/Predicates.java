package com.github.debris.debrisclient.util;

import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

public class Predicates {
    public static boolean notInGame(Minecraft client) {
        return client.level == null || client.player == null;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean notInGuiContainer(Minecraft client) {
        if (notInGame(client)) return true;
        if (client.player.isSpectator()) return true;
        Screen currentScreen = GuiUtils.getCurrentScreen();
        if (currentScreen instanceof AbstractContainerScreen<?>) {// container screen
            if (currentScreen instanceof CreativeModeInventoryScreen creativeInventoryScreen && !creativeInventoryScreen.isInventoryOpen()) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean inGameNoGui(Minecraft client) {
        if (notInGame(client)) return false;
        return client.screen == null;
    }

}
