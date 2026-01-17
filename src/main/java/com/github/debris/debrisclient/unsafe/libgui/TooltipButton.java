package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.network.chat.Component;

public class TooltipButton extends WButton {
    private Component[] tooltips = new Component[]{};

    public TooltipButton(Component label) {
        super(label);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        super.addTooltip(tooltip);
        tooltip.add(this.tooltips);
    }

    public TooltipButton setTooltips(Component... tooltips) {
        this.tooltips = tooltips;
        return this;
    }
}
