package com.github.debris.debrisclient.mixin.compat.libgui;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.feat.ProgressResume;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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

@Restriction(require = @Condition(ModReference.LibGui))
@Mixin(value = CottonClientScreen.class, remap = false)
public abstract class CottonClientScreenMixin extends Screen {
    protected CottonClientScreenMixin(Component title) {
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
