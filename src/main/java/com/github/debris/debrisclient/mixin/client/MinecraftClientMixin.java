package com.github.debris.debrisclient.mixin.client;

import com.github.debris.debrisclient.listener.TickListener;
import com.github.debris.debrisclient.util.HighlightUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderTick(boolean tick, CallbackInfo ci) {
        if (this.player != null && this.world != null) {
            TickListener.onRenderTick((MinecraftClient) (Object) this);
        }
    }

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void forceOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (HighlightUtil.shouldHighlightEntity(entity.getType())) cir.setReturnValue(true);
    }
}
