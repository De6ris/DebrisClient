package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.mixin.client.gui.IMixinChatHud;
import com.github.Debris.DebrisClient.mixin.client.gui.IMixinGuiContainer;
import com.github.Debris.DebrisClient.mixin.client.IMixinMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class AccessorUtil {
    public static Slot getHoveredSlot(HandledScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getHoveredSlot();
    }

    public static int getGuiLeft(HandledScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getGuiLeft();
    }

    public static List<ChatHudLine.Visible> getVisibleMessages(ChatHud chatHud) {
        return ((IMixinChatHud) chatHud).getVisibleMessages();
    }

    public static void use(MinecraftClient client) {
        ((IMixinMinecraftClient) client).invokeDoItemUse();
    }

    public static boolean attack(MinecraftClient client) {
        return ((IMixinMinecraftClient) client).invokeDoAttack();
    }
}
