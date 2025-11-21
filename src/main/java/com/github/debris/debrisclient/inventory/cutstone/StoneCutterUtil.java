package com.github.debris.debrisclient.inventory.cutstone;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.inventory.section.SectionHandler;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StoneCutterUtil {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isStoneCutterRecipeViewOpen() {
        return GuiUtils.getCurrentScreen() != null &&
                DCCommonConfig.StoneCutterRecipeView.getKeybind().isKeybindHeld() &&
                isStoneCutterGui();
    }

    public static boolean isStoneCutterGui() {
        return GuiUtils.getCurrentScreen() instanceof StonecutterScreen;
    }

    public static boolean isOverStoneCutterResult() {
        Optional<ContainerSection> sectionMouseOver = SectionHandler.getSectionMouseOver();
        if (sectionMouseOver.isEmpty()) return false;
        ContainerSection containerSection = sectionMouseOver.get();
        return containerSection.isOf(EnumSection.CraftResult);
    }

    @SuppressWarnings("ConstantConditions")
    private static void chooseRecipe(ItemStack result) {
        StonecutterMenu container = (StonecutterMenu) InventoryUtil.getCurrentContainer();
        List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> entries = container.getVisibleRecipes().entries();
        ContextMap contextParameterMap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
        for (int i = 0; i < entries.size(); i++) {
            ItemStack recipeResult = entries.get(i).recipe().optionDisplay().resolveForFirstStack(contextParameterMap);
            if (ItemUtil.compareIDMeta(result, recipeResult)) {
                container.clickMenuButton(Minecraft.getInstance().player, i);
                InventoryUtil.clickButton(i);
                return;// success
            }
        }
    }

    public static void cutStone() {
        runInternal(InventoryUtil::quickMove);
    }

    public static void cutStoneThenDrop() {
        runInternal(InventoryUtil::dropStack);
    }

    private static void runInternal(Consumer<Slot> outputAction) {
        StoneCutterRecipePattern selectedRecipe = StoneCutterRecipeStorage.getInstance().getSelectedRecipe();
        if (!selectedRecipe.isValid()) return;
        ItemStack input = selectedRecipe.getInput();
        ItemStack result = selectedRecipe.getResult();

        ContainerSection playerInventory = EnumSection.InventoryWhole.get();

        InventoryTweaks.clearCursor(playerInventory);

        InventoryUtil.dropAllMatching(ItemUtil.predicateIDMeta(result));// first throw those crafting result

        clearInputSlot();

        Slot outputSlot = EnumSection.CraftResult.get().getFirstSlot();

        playerInventory.predicateRun(ItemUtil.predicateIDMeta(input), slot -> {
            InventoryUtil.quickMove(slot);
            chooseRecipe(result);
            outputAction.accept(outputSlot);
        });
    }

    private static void clearInputSlot() {
        Slot input = EnumSection.StoneCutterIn.get().getFirstSlot();
        if (input.hasItem()) {
            InventoryUtil.quickMove(input);
        }

        InventoryUtil.dropStackIfPossible(input);
    }

}
