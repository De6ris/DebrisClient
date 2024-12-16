package com.github.Debris.DebrisClient.mixin.render;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> boolean preventFrameRendering(boolean original, @Local(argsOnly = true) E entity) {
        EntityType<?> type = entity.getType();
        boolean isFrame = type == EntityType.GLOW_ITEM_FRAME || type == EntityType.ITEM_FRAME;
        if (original && DCCommonConfig.CancelFrameRendering.getBooleanValue() && isFrame) {
            return false;
        }
        return original;
    }
}
