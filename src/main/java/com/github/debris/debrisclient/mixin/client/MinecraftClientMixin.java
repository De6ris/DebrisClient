package com.github.debris.debrisclient.mixin.client;

import com.github.debris.debrisclient.listener.TickListener;
import com.github.debris.debrisclient.util.HighlightUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onRenderTick(boolean tick, CallbackInfo ci) {
        if (this.player != null && this.level != null) {
            TickListener.onRenderTick((Minecraft) (Object) this);
        }
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void forceOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (HighlightUtil.shouldHighlightEntity(entity.getType())) cir.setReturnValue(true);
    }
}
