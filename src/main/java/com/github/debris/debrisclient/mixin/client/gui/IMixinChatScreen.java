package com.github.debris.debrisclient.mixin.client.gui;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChatScreen.class)
public interface IMixinChatScreen {
    @Accessor("input")
    EditBox getChatField();
}
