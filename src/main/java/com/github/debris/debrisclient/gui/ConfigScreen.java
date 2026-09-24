package com.github.debris.debrisclient.gui;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ConfigScreen extends GuiConfigsBase {
    private final List<ConfigTab> tabs;
    private ConfigTab currentTab;

    public ConfigScreen(String configHandlerId, Component title, List<ConfigTab> tabs) {
        super(10, 50, configHandlerId, null, "dummy");
        this.title = title.getString();
        this.tabs = tabs;
        this.currentTab = tabs.getFirst();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        for (ConfigTab tab : this.tabs) {
            x += this.createButton(x, y, -1, tab);
        }
    }

    private int createButton(int x, int y, int width, ConfigTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.currentTab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(this.currentTab.configs());
    }

    private record ButtonListener(ConfigTab tab, ConfigScreen parent) implements IButtonActionListener {
        @SuppressWarnings("DataFlowIssue")
        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            this.parent.currentTab = this.tab;
            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }
}
