package com.github.debris.debrisclient.mixin.world.entity;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    private void cheatEffect(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (
                (Object) this == Minecraft.getInstance().player
                        && DCCommonConfig.DarknessOverride.getBooleanValue()
                        && (effect == MobEffects.DARKNESS || effect == MobEffects.BLINDNESS)
        ) {
            cir.setReturnValue(false);
        }
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    private void cheatEffect1(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (
                (Object) this == Minecraft.getInstance().player
                        && DCCommonConfig.DarknessOverride.getBooleanValue()
                        && (effect == MobEffects.DARKNESS || effect == MobEffects.BLINDNESS)
        ) {
            cir.setReturnValue(null);
        }
    }
}
