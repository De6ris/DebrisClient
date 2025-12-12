package com.github.debris.debrisclient.mixin.compat.malilib;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.util.InputUtil;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.render.GuiContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiScrollBar.class, remap = false)
public abstract class GuiScrollBarMixin {
    @Shadow
    protected boolean dragging;

    @Shadow
    public abstract void setValue(int value);

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/gui/GuiScrollBar;handleDrag(II)V", remap = false),
            remap = true
    )
    private void enhance(GuiContext ctx, int mouseX, int mouseY, float partialTicks, int xPosition, int yPosition, int width, int height, int totalHeight, CallbackInfo ci) {
        if (DCCommonConfig.ScrollerEnhance.getBooleanValue()) {// TODO unmatch when list too large
            boolean inRange = mouseX > xPosition && mouseX < xPosition + width && mouseY > yPosition && mouseY < yPosition + height;
            if (inRange && !this.dragging && InputUtil.isLeftClicking()) {
                float ratio = (float) (mouseY - yPosition) / height;
                this.setValue((int) (100 * ratio));
            }
        }
    }
}
