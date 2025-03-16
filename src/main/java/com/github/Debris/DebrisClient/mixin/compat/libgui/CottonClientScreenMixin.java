package com.github.Debris.DebrisClient.mixin.compat.libgui;

import com.github.Debris.DebrisClient.DebrisClient;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.ProgressResume;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.msdnicrosoft.commandbuttons.gui.CommandGUI;
import work.msdnicrosoft.commandbuttons.gui.CommandListPanel;

import java.util.List;
import java.util.function.Consumer;

@Mixin(value = CottonClientScreen.class, remap = false)
public abstract class CottonClientScreenMixin extends Screen {
    protected CottonClientScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    public abstract GuiDescription getDescription();

    @Inject(method = "init", at = @At("RETURN"), remap = true)
    private void onInit(CallbackInfo ci) {
        if (DCCommonConfig.ProgressResuming.getBooleanValue()) {
            this.findScrollBarAndRun(x -> ProgressResume.getProgress(this).ifPresent(x::setValue));
        }
    }

    @Inject(method = "removed", at = @At("RETURN"), remap = true)
    private void onRemoved(CallbackInfo ci) {
        if (DCCommonConfig.ProgressResuming.getBooleanValue()) {
            this.findScrollBarAndRun(x -> ProgressResume.saveProgress(this, x.getValue()));
        }
    }

    @Unique
    private void findScrollBarAndRun(Consumer<WScrollBar> scrollBarAction) {
        GuiDescription description = this.getDescription();
        if (description instanceof CommandGUI commandGUI) {
            List<? extends CommandListPanel<?, ?>> listPanels = commandGUI.getRootPanel().streamChildren()
                    .filter(x -> x instanceof CommandListPanel<?, ?>)
                    .map(x -> (CommandListPanel<?, ?>) x)
                    .toList();
            if (listPanels.size() != 1) {
                DebrisClient.logger.warn("Mixin CottonClientScreen: Why CommandGUI contains 0 or >1 list panel");
            } else {
                scrollBarAction.accept(listPanels.getFirst().getScrollBar());
            }
        }
    }
}
