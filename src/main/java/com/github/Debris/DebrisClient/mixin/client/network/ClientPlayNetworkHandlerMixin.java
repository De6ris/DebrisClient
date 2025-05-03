package com.github.Debris.DebrisClient.mixin.client.network;

import com.github.Debris.DebrisClient.feat.Hooks;
import com.github.Debris.DebrisClient.inventory.autoprocess.AutoProcessManager;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(
            method = "onSetTradeOffers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/MerchantScreenHandler;setCanRefreshTrades(Z)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onTradeInfoUpdate(CallbackInfo ci) {
        Hooks.onTradeInfoUpdate(this.client);
    }// This injection point from tweakemore

    @Inject(method = "onOpenScreen", at = @At("RETURN"))
    private void onContainerOpen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        Screen screen = GuiUtils.getCurrentScreen();
        if (screen instanceof HandledScreen<?> guiContainer) {
            AutoProcessManager.onGuiContainerOpen(guiContainer);
        }
    }
}
