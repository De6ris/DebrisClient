package com.github.Debris.DebrisClient.mixin.world;

import com.github.Debris.DebrisClient.feat.WorldState;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow
    public abstract float getThunderGradient(float delta);

    @Inject(method = "setThunderGradient", at = @At("RETURN"))
    private void onThunderSync(float thunderGradient, CallbackInfo ci) {
        WorldState.onThunderSync((double) this.getThunderGradient(1.0F) > 0.9);
    }
}
