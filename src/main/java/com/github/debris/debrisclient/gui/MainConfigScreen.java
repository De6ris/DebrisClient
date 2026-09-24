package com.github.debris.debrisclient.gui;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.config.HideConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import static com.github.debris.debrisclient.DebrisClient.MOD_NAME;
import static com.github.debris.debrisclient.DebrisClient.MOD_VERSION;

public class MainConfigScreen extends ConfigScreen {
    private static final MainConfigScreen INSTANCE = new MainConfigScreen();

    public static ConfigScreen getInstance(@Nullable Screen parent) {
        INSTANCE.setParent(parent);
        return INSTANCE;
    }

    private MainConfigScreen() {
        super(
                DCCommonConfig.ID.toString(),
                Component.translatable("debrisclient.gui.title.configs", MOD_NAME, MOD_VERSION),
                ImmutableList.of(
                        new ConfigTab("全部", DCCommonConfig.ALL_CONFIGS),
                        new ConfigTab("值", DCCommonConfig.Values),
                        new ConfigTab("联动", HideConfig.filter(DCCommonConfig.Integration)),
                        new ConfigTab("列表", DCCommonConfig.Lists),
                        new ConfigTab("热键", DCCommonConfig.KeyPress),
                        new ConfigTab("切换", DCCommonConfig.KeyToggle),
                        new ConfigTab("禁用", DCCommonConfig.Yeets),
                        new ConfigTab("高亮", DCCommonConfig.Highlights)
                )
        );
    }
}
