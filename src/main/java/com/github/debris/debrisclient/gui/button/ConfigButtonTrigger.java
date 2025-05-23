package com.github.debris.debrisclient.gui.button;

import fi.dy.masa.malilib.gui.button.ButtonGeneric;

public class ConfigButtonTrigger extends ButtonGeneric {
    /**
     * -1 means auto
     */
    public ConfigButtonTrigger(int x, int y) {
        super(x, y, -1, 20, "触发", "这将模拟按键按下和松开");
    }
}
