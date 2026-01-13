package com.github.debris.debrisclient.mixin.compat.journeymap;

import com.github.debris.debrisclient.compat.ModReference;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import journeymap.client.event.FabricEventHandlerManager;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(value = ModReference.JourneyMap))
@Mixin(value = FabricEventHandlerManager.class, remap = false)
public class FabricEventHandlerManagerMixin {
    @WrapOperation(
            method = "lambda$registerFabricEvents$8",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/PlayerChatMessage;signature()Lnet/minecraft/network/chat/MessageSignature;",
                    remap = true
            ),
            remap = false
    )
    private static MessageSignature fixNPE(PlayerChatMessage instance, Operation<MessageSignature> original) {
        if (instance == null) return null;
        return original.call(instance);
    }
}
