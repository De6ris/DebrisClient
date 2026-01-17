package com.github.debris.debrisclient.unsafe.libgui;

import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.joml.Vector4i;

import java.util.IdentityHashMap;
import java.util.Map;

public class SuppressivePanel extends WGridPanel {
    private final Map<WWidget, Vector4i> HISTORY = new IdentityHashMap<>();

    public SuppressivePanel(int gridSize) {
        super(gridSize);
    }

    @Override
    public void add(WWidget w, int x, int y, int width, int height) {
        super.add(w, x, y, width, height);
        HISTORY.put(w, new Vector4i(x, y, width, height));
    }

    public void setVisible(WWidget w, boolean visible) {
        if (visible) {
            Vector4i vector4i = HISTORY.get(w);
            if (vector4i != null) {
                this.add(w, vector4i.x, vector4i.y, vector4i.z, vector4i.w);
                this.validate(this.getHost());
            }
        } else {
            this.remove(w);
            this.validate(this.getHost());
        }
    }
}
