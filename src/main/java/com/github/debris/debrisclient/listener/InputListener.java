package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeRenderer;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.feat.QuickBundle;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import com.github.debris.debrisclient.util.InputUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.InputUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;

import java.util.Optional;

public class InputListener implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {
    private static final InputListener INSTANCE = new InputListener();
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final IntSet BUTTON_UP_CANCEL_SET = new IntOpenHashSet();

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
        for (IHotkey hotkey : DCCommonConfig.Highlights) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(DebrisClient.MOD_NAME, "按下式", DCCommonConfig.KeyPress);
        manager.addHotkeysForCategory(DebrisClient.MOD_NAME, "切换式", DCCommonConfig.KeyToggle);
        manager.addHotkeysForCategory(DebrisClient.MOD_NAME, "禁用", DCCommonConfig.Yeets);
        manager.addHotkeysForCategory(DebrisClient.MOD_NAME, "高亮", DCCommonConfig.Highlights);
    }

    @Override
    public boolean onMouseClick(Click click, boolean eventButtonState) {
        int mouseX = InputUtils.getMouseX();
        int mouseY = InputUtils.getMouseY();
        if (eventButtonState) {
            return this.handleButtonDown(mouseX, mouseY, click);
        } else {
            return this.handleButtonUp(mouseX, mouseY, click);
        }
    }

    private boolean handleButtonDown(int mouseX, int mouseY, Click click) {
        int eventButton = click.button();
        if (this.client.options.attackKey.matchesMouse(click)) {

            if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
                StoneCutterRecipeStorage.getInstance().setCurrentSelected(StoneCutterRecipeRenderer.getInstance().getHoveredRecipeId(mouseX, mouseY, InventoryUtil.getGuiContainer()));
                return true;
            }

            if (Predicates.notInGuiContainer(this.client))
                return false;// the below assuming valid environment

            if (DCCommonConfig.ModifierMoveAll.getKeybind().isKeybindHeld()) {
                Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
                if (optional.isPresent()) {
                    optional.get().notEmptyRun(InventoryUtil::quickMove);
                    return true;
                }
            }

            if (DCCommonConfig.ModifierSpreadItem.getKeybind().isKeybindHeld()) {
                if (InventoryTweaks.trySpreading(false)) {
                    this.cancelButtonUp(eventButton);
                    return true;
                }
            }

            if (DCCommonConfig.ModifierMoveSimilar.getKeybind().isKeybindHeld()) {
                if (InventoryTweaks.tryMoveSimilar()) {
                    return true;// cancel this click
                }
            }

        }


        if (this.client.options.useKey.matchesMouse(click)) {

            if (Predicates.notInGuiContainer(this.client))
                return false;// the below assuming valid environment

            if (DCCommonConfig.ModifierSpreadItem.getKeybind().isKeybindHeld()) {
                if (InventoryTweaks.trySpreading(true)) {
                    this.cancelButtonUp(eventButton);// will put down one at HandledScreen.mouseReleased if not canceled
                    return true;
                }
            }

            if (DCCommonConfig.ModifierClearBundle.getKeybind().isKeybindHeld()) {
                if (QuickBundle.tryClearBundle()) {
                    this.cancelButtonUp(eventButton);
                    return true;// cancel this click
                }
            }

        }

        return false;
    }

    private boolean handleButtonUp(int mouseX, int mouseY, Click click) {
        int button = click.button();
        if (BUTTON_UP_CANCEL_SET.contains(button)) {
            BUTTON_UP_CANCEL_SET.remove(button);
            return true;
        }
        return false;
    }

    private void cancelButtonUp(int eventButton) {
        BUTTON_UP_CANCEL_SET.add(eventButton);
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double amount) {
        if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
            if (amount != 0) {
                StoneCutterRecipeStorage.getInstance().scrollSelection(amount < 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseMove(double mouseX, double mouseY) {
        if (Predicates.notInGuiContainer(this.client))
            return;// the below assuming valid environment

        if (InputUtil.isLeftClicking()) {// left click down

            if (DCCommonConfig.ModifierMoveSimilar.getKeybind().isKeybindHeld()) {
                InventoryTweaks.tryMoveSimilar();
            }

        }
    }
}
