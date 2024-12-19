package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.DebrisClient;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterRecipeRenderer;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterRecipeStorage;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.hotkeys.*;
import net.minecraft.client.MinecraftClient;

import java.util.Optional;

public class InputListener implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {
    private static final InputListener INSTANCE = new InputListener();

    public static InputListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : DCCommonConfig.KeyPress) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
        for (IHotkey hotkey : DCCommonConfig.KeyToggle) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
        for (IHotkey hotkey : DCCommonConfig.Yeets) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(DebrisClient.MOD_ID, "按下式", DCCommonConfig.KeyPress);
        manager.addHotkeysForCategory(DebrisClient.MOD_ID, "切换式", DCCommonConfig.KeyToggle);
        manager.addHotkeysForCategory(DebrisClient.MOD_ID, "禁用", DCCommonConfig.Yeets);
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState) {
        if (eventButtonState && MinecraftClient.getInstance().options.attackKey.matchesMouse(eventButton)) {// left click down

            if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
                StoneCutterRecipeStorage.getInstance().setCurrentSelected(StoneCutterRecipeRenderer.getInstance().getHoveredRecipeId(mouseX, mouseY, InventoryUtil.getGuiContainer()));
                return true;
            }

            if (Predicates.notInGuiContainer(MinecraftClient.getInstance())) return false;// the below assuming valid environment

            if (DCCommonConfig.ModifierMoveAll.getKeybind().isKeybindHeld()) {
                Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
                if (optional.isPresent()) {
                    optional.get().predicateRun(x -> true, InventoryUtil::quickMove);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, double amount) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            if (amount != 0) {
                StoneCutterRecipeStorage.getInstance().scrollSelection(amount < 0);
                return true;
            }
        }
        return false;
    }
}
