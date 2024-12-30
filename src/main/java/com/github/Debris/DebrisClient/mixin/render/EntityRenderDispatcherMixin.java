package com.github.Debris.DebrisClient.mixin.render;

import com.github.Debris.DebrisClient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> boolean preventFrameRendering(boolean original, @Local(argsOnly = true) E entity) {
        if (!original) return false;
        if (CullingUtil.shouldCullEntity(entity.getType())) return false;
        return true;
    }
}
