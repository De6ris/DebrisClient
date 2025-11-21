package com.github.debris.debrisclient.mixin.client.network;

import com.github.debris.debrisclient.feat.advancement.AdventuringTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementManagerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "update", at = @At("RETURN"))
    private void onAdvancements(ClientboundUpdateAdvancementsPacket packet, CallbackInfo ci) {
        AdventuringTimeHelper.onProgressUpdate(this.minecraft, packet.getProgress());
    }
}
