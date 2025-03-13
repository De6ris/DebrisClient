package com.github.Debris.DebrisClient.mixin.client.gui;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatHud.class)
public interface IMixinChatHud {
    @Accessor
    List<ChatHudLine.Visible> getVisibleMessages();
}
