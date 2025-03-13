package com.github.Debris.DebrisClient.mixin.world;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.ChatUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.dimension.PortalForcer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @SuppressWarnings("all")
    @Inject(method = "createPortal", at = @At(value = "RETURN", ordinal = 1))
    private void onPortalCreated(BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<Optional<BlockLocating.Rectangle>> cir) {
        if (DCCommonConfig.MonitorPortalGeneration.getBooleanValue()) {
            String string = cir.getReturnValue().get().lowerLeft.toString();
            ChatUtil.addLocalMessage(Text.empty().append(Text.translatable("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD)).append(ScreenTexts.SPACE).append(string));
        }
    }
}
