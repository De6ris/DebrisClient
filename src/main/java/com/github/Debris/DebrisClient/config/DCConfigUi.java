package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.config.early.DCEarlyConfig;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

import java.util.List;

import static com.github.Debris.DebrisClient.DebrisClient.MOD_NAME;
import static com.github.Debris.DebrisClient.DebrisClient.MOD_VERSION;

public class DCConfigUi extends GuiConfigsBase {

    private static Tab tab = Tab.ALL;

    public DCConfigUi() {
        super(10, 50, MOD_NAME, null, "debris_client.gui.title.configs", MOD_NAME, MOD_VERSION);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;
        for (Tab tab : Tab.values()) {
            x += this.createButton(x, y, -1, tab);
        }

    }

    private int createButton(int x, int y, int width, Tab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.name);
        button.setEnabled(DCConfigUi.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        Tab tab = DCConfigUi.tab;

        configs = switch (tab) {
            case VALUE -> DCCommonConfig.Values;
            case Fix -> DCCommonConfig.Fix;
            case Lists -> DCCommonConfig.Lists;
            case PRESS -> DCCommonConfig.KeyPress;
            case TOGGLE -> DCCommonConfig.KeyToggle;
            case Yeets -> DCCommonConfig.Yeets;
            default -> DCCommonConfig.ALL_CONFIGS;
        };

        return ConfigOptionWrapper.createFor(configs);
    }

    @Override
    public void removed() {
        super.removed();
        DCEarlyConfig.getInstance().refresh();
    }

    private static class ButtonListener implements IButtonActionListener {
        private final DCConfigUi parent;
        private final Tab tab;

        public ButtonListener(Tab tab, DCConfigUi parent) {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            DCConfigUi.tab = this.tab;
            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum Tab {
        ALL("全部"),
        VALUE("值"),
        Fix("修复"),
        Lists("列表"),
        PRESS("按下"),
        TOGGLE("切换"),
        Yeets("禁用"),
        ;

        public final String name;

        Tab(String str) {
            name = str;
        }
    }
}
