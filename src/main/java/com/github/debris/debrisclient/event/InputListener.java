package com.github.debris.debrisclient.event;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.config.InventoryConfig;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeRenderer;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import com.github.debris.debrisclient.inventory.cutstone.StoneCutterUtil;
import com.github.debris.debrisclient.inventory.feat.HoldInventoryMoving;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.feat.QuickBundle;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.input.InputUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InputListener implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {
    private static final InputListener INSTANCE = new InputListener();
    private final Minecraft client = Minecraft.getInstance();
    private final IntSet BUTTON_UP_CANCEL_SET = new IntOpenHashSet();

    public static InputListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        Stream<List<? extends IHotkey>> stream = Stream.of(
                DCCommonConfig.KeyPress,
                DCCommonConfig.KeyToggle,
                DCCommonConfig.Yeets,
                DCCommonConfig.Highlights,
                InventoryConfig.HOTKEY,
                InventoryConfig.TOGGLE
        );
        stream.flatMap(Collection::stream).forEach(x -> manager.addKeybindToMap(x.getKeybind()));
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        String id = DCCommonConfig.ID.toString();
        manager.addHotkeysForCategory(id, "热键", DCCommonConfig.KeyPress);
        manager.addHotkeysForCategory(id, "切换", DCCommonConfig.KeyToggle);
        manager.addHotkeysForCategory(id, "禁用", DCCommonConfig.Yeets);
        manager.addHotkeysForCategory(id, "高亮", DCCommonConfig.Highlights);

        id = InventoryConfig.ID.toString();
        manager.addHotkeysForCategory(id, "热键", InventoryConfig.HOTKEY);
        manager.addHotkeysForCategory(id, "切换", InventoryConfig.TOGGLE);
    }

    @Override
    public boolean onMouseClick(MouseButtonEvent click, boolean eventButtonState) {
        int mouseX = InputUtils.getMouseX();
        int mouseY = InputUtils.getMouseY();
        if (eventButtonState) {
            return this.handleButtonDown(mouseX, mouseY, click);
        } else {
            return this.handleButtonUp(mouseX, mouseY, click);
        }
    }

    private boolean handleButtonDown(int mouseX, int mouseY, MouseButtonEvent click) {
        int eventButton = click.button();
        if (this.client.options.keyAttack.matchesMouse(click)) {

            if (StoneCutterUtil.isStoneCutterRecipeViewOpen()) {
                StoneCutterRecipeStorage.getInstance().setCurrentSelected(StoneCutterRecipeRenderer.getInstance().getHoveredRecipeId(mouseX, mouseY, InventoryUtil.getGuiContainer()));
                return true;
            }

            if (Predicates.notInGuiContainer(this.client))
                return false;// the below assuming valid environment

            if (InventoryConfig.ModifierMoveAll.getKeybind().isKeybindHeld()) {
                Optional<ContainerSection> optional = SectionHandler.getSectionMouseOver();
                if (optional.isPresent()) {
                    optional.get().notEmptyRun(InventoryUtil::quickMove);
                    return true;
                }
            }

            if (InventoryConfig.ModifierSpreadItem.getKeybind().isKeybindHeld()) {
                if (InventoryTweaks.trySpreading(false)) {
                    this.cancelButtonUp(eventButton);
                    return true;
                }
            }

            if (HoldInventoryMoving.start()) {
                return true;
            }

        }


        if (this.client.options.keyUse.matchesMouse(click)) {

            if (Predicates.notInGuiContainer(this.client))
                return false;// the below assuming valid environment

            if (InventoryConfig.ModifierSpreadItem.getKeybind().isKeybindHeld()) {
                if (InventoryTweaks.trySpreading(true)) {
                    this.cancelButtonUp(eventButton);// will put down one at HandledScreen.mouseReleased if not canceled
                    return true;
                }
            }

            if (InventoryConfig.ModifierClearBundle.getKeybind().isKeybindHeld()) {
                if (QuickBundle.tryClearBundle()) {
                    this.cancelButtonUp(eventButton);
                    return true;// cancel this click
                }
            }

        }

        return false;
    }

    private boolean handleButtonUp(int mouseX, int mouseY, MouseButtonEvent click) {
        int button = click.button();

        if (this.client.options.keyAttack.matchesMouse(click)) {
            if (Predicates.notInGuiContainer(this.client))
                return false;// the below assuming valid environment

            HoldInventoryMoving.stop();
        }

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
        if (Predicates.notInGuiContainer(this.client)) return;// the below assuming valid environment

        HoldInventoryMoving.mouseMove();
    }
}
