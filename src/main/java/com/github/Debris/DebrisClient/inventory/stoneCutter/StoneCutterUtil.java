package com.github.Debris.DebrisClient.inventory.stoneCutter;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.section.SectionHandler;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

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
        return EnumSection.CraftResult.isOf(containerSection);
    }

    private static void chooseRecipe(ItemStack result) {
        StonecutterScreen guiContainer = (StonecutterScreen) InventoryUtil.getGuiContainer();
        StonecutterScreenHandler container = guiContainer.getScreenHandler();
        List<CuttingRecipeDisplay.GroupEntry<StonecuttingRecipe>> entries = container.getAvailableRecipes().entries();
        assert MinecraftClient.getInstance().world != null;
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters(MinecraftClient.getInstance().world);
        for (int i = 0; i < entries.size(); i++) {
            ItemStack recipeResult = entries.get(i).recipe().optionDisplay().getFirst(contextParameterMap);
            if (ItemUtil.compareIDMeta(result, recipeResult)) {
                container.onButtonClick(MinecraftClient.getInstance().player, i);
                InventoryUtil.clickButton(i);
                return;// success
            }
        }
    }

    public static void cutStone() {
        StoneCutterRecipePattern selectedRecipe = StoneCutterRecipeStorage.getInstance().getSelectedRecipe();
        if (!selectedRecipe.isValid()) return;
        ItemStack input = selectedRecipe.getInput();
        ItemStack result = selectedRecipe.getResult();

        Slot stoneCutterInput = EnumSection.StoneCutterIn.get().slots().getFirst();
        Slot stoneCutterOutput = EnumSection.CraftResult.get().slots().getFirst();

        InventoryTweaks.makeSureNotHoldingItem(EnumSection.InventoryWhole.get());

        if (stoneCutterInput.hasStack()) {// deal with slot occupied
            InventoryUtil.quickMove(stoneCutterInput);// move to inventory
        }

        if (stoneCutterInput.hasStack()) {// if still, then drop
            InventoryUtil.drop(stoneCutterInput, true);
        }

        EnumSection.InventoryWhole.get().predicateRun(ItemUtil.predicateIDMeta(input), x -> {
            InventoryUtil.quickMove(x);
            chooseRecipe(result);
            InventoryUtil.quickMove(stoneCutterOutput);
        });
    }

    public static void cutStoneThenDrop() {
        StoneCutterRecipePattern selectedRecipe = StoneCutterRecipeStorage.getInstance().getSelectedRecipe();
        if (!selectedRecipe.isValid()) return;
        ItemStack input = selectedRecipe.getInput();
        ItemStack result = selectedRecipe.getResult();

        Slot stoneCutterInput = EnumSection.StoneCutterIn.get().slots().getFirst();
        Slot stoneCutterOutput = EnumSection.CraftResult.get().slots().getFirst();

        ContainerSection playerInventory = EnumSection.InventoryWhole.get();

        InventoryTweaks.makeSureNotHoldingItem(playerInventory);

        InventoryUtil.dropAllMatching(ItemUtil.predicateIDMeta(result));// first throw those crafting result

        if (stoneCutterInput.hasStack()) {// deal with slot occupied
            InventoryUtil.quickMove(stoneCutterInput);
        }

        if (stoneCutterInput.hasStack()) {
            InventoryUtil.drop(stoneCutterInput, true);// force clear
        }

        for (Slot slot : playerInventory.slots()) {
            if (ItemUtil.compareIDMeta(slot.getStack(), input)) {// can craft
                InventoryUtil.quickMove(slot);
                chooseRecipe(result);
                InventoryUtil.quickMove(stoneCutterOutput);
            }
            if (ItemUtil.compareIDMeta(slot.getStack(), result)) {// should drop
                InventoryUtil.drop(slot, true);
            }
        }
    }
}
