package com.github.debris.debrisclient.mixin.world;

import com.github.debris.debrisclient.feat.log.GameLogs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @Shadow
    @Final
    private ServerLevel level;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Inject(method = "createPortal", at = @At(value = "RETURN", ordinal = 1))
    private void onPortalCreated(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        GameLogs.PORTAL.onPortalCreated(this.level, cir.getReturnValue().get().minCorner);
    }
}
