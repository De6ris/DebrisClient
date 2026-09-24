package com.github.debris.debrisclient.gui;

import com.github.debris.debrisclient.config.InventoryConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class InventoryConfigScreen extends ConfigScreen {
    private static final InventoryConfigScreen INSTANCE = new InventoryConfigScreen();

    public static ConfigScreen getInstance(@Nullable Screen parent) {
        INSTANCE.setParent(parent);
        return INSTANCE;
    }

    private InventoryConfigScreen() {
        super(
                InventoryConfig.ID.toString(),
                Component.literal("DebrisClient Inventory Config - 1.0.0"),
                ImmutableList.of(
                        new ConfigTab("全部", InventoryConfig.ALL_CONFIGS),
                        new ConfigTab("值", InventoryConfig.VALUES),
                        new ConfigTab("列表", InventoryConfig.LISTS),
                        new ConfigTab("热键", InventoryConfig.HOTKEY),
                        new ConfigTab("切换", InventoryConfig.TOGGLE)
                )
        );
    }
}
