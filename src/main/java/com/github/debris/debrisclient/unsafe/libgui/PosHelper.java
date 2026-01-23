package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.WWidget;

public class PosHelper {
    private final WGridPanel root;
    private int x = 0;
    private int y = 0;

    public PosHelper(WGridPanel root) {
        this.root = root;
    }

    public void newline() {
        this.x = 0;
        this.y++;
    }

    public void newline(int height) {
        this.x = 0;
        this.y += height;
    }

    public void home() {
        this.x = 0;
    }

    public void putWidget(WWidget widget, int width, int height) {
        this.putWidget(widget, width, height, 0);
    }

    public void putWidget(WWidget widget, int width, int height, int gap) {
        this.root.add(widget, x, y, width, height);
        x += width + gap;
    }

    public void putText(WText text, int yShift, int width) {
        this.root.add(text, x, y + yShift, width, 1);
        this.x += width;
    }
}
