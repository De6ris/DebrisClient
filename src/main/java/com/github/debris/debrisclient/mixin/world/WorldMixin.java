package com.github.debris.debrisclient.mixin.world;

import com.github.debris.debrisclient.feat.log.GameLogs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class WorldMixin {
    @Shadow
    public abstract float getThunderLevel(float delta);

    @Inject(method = "setThunderLevel", at = @At("RETURN"))
    private void onThunderSync(float thunderGradient, CallbackInfo ci) {
        GameLogs.THUNDER.onThunderSync((double) this.getThunderLevel(1.0F) > 0.9);
    }
}
