package com.github.Debris.DebrisClient.mixin.network;

import com.github.Debris.DebrisClient.util.MiscUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
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
    private void autoVillagerTradeFavorites(CallbackInfo ci) {
        MiscUtil.runOrientedTrading(this.client);
    }// This injection point from tweakemore
}
