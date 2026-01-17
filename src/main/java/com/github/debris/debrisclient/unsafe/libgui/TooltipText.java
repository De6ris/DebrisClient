package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WText;
import net.minecraft.network.chat.Component;

public class TooltipText extends WText {
    private Component[] tooltips = new Component[]{};

    public TooltipText(Component text, int color) {
        super(text, color);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        super.addTooltip(tooltip);
        tooltip.add(this.tooltips);
    }

    public TooltipText setTooltips(Component... tooltips) {
        this.tooltips = tooltips;
        return this;
    }
}
