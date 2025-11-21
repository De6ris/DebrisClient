package com.github.debris.debrisclient.mixin.client.gui;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface IMixinChatHud {
    @Accessor
    List<GuiMessage.Line> getTrimmedMessages();
}
