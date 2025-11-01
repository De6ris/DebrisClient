package com.github.debris.debrisclient.mixin.client.network;

import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onAdvancements", at = @At("RETURN"))
    private void onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        AdventuringTimeHelper.onProgressUpdate(this.client, packet.getAdvancementsToProgress());
    }
}
