package com.github.debris.debrisclient.mixin.client.render;

import com.github.debris.debrisclient.util.CullingUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderManager.class)
public class EntityRenderDispatcherMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private <E extends Entity> boolean cullEntity(boolean original, @Local(argsOnly = true) E entity) {
        return original && !CullingUtil.shouldCullEntity(entity.getType());
    }
}
