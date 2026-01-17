package com.github.debris.debrisclient.unsafe.libgui;

import com.github.debris.debrisclient.feat.commandmacro.YPosMode;
import io.github.cottonmc.cotton.gui.widget.WButton;

public class YPosModeButton extends WButton {
    private YPosMode mode = YPosMode.SURFACE;

    public YPosModeButton() {
        super(YPosMode.SURFACE.label());
    }

    public void toggle() {
        this.mode = this.mode.next();
        this.setLabel(this.mode.label());
    }

    public YPosMode getMode() {
        return mode;
    }
}
