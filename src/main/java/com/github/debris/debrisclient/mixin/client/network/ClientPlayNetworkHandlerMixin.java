package com.github.debris.debrisclient.mixin.client.network;

import com.github.debris.debrisclient.feat.Hooks;
import com.github.debris.debrisclient.inventory.autoprocess.AutoProcessManager;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonPacketListenerImpl {
    protected ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(
            method = "handleMerchantOffers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/MerchantMenu;setCanRestock(Z)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onTradeInfoUpdate(CallbackInfo ci) {
        Hooks.onTradeInfoUpdate(this.minecraft);
    }// This injection point from tweakemore

    @Inject(method = "handleOpenScreen", at = @At("RETURN"))
    private void onContainerOpen(ClientboundOpenScreenPacket packet, CallbackInfo ci) {
        Screen screen = GuiUtils.getCurrentScreen();
        if (screen instanceof AbstractContainerScreen<?> guiContainer) {
            AutoProcessManager.onGuiContainerOpen(guiContainer);
        }
    }
}
