package com.github.debris.debrisclient.compat;

import com.github.debris.debrisclient.gui.DCConfigUi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            DCConfigUi ui = new DCConfigUi();
            ui.setParent(screen);
            return ui;
        };
    }
}