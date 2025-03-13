package com.github.Debris.DebrisClient.mixin.client;

import com.github.Debris.DebrisClient.listener.TickListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
