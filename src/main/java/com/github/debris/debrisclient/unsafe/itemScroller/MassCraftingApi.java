package com.github.debris.debrisclient.unsafe.itemScroller;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.inventory.feat.InventoryTweaks;
import com.github.debris.debrisclient.inventory.section.ContainerSection;
import com.github.debris.debrisclient.inventory.section.EnumSection;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import fi.dy.masa.itemscroller.recipes.RecipeStorage;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class MassCraftingApi {
    public static boolean isCraftingGui() {
        Screen currentScreen = GuiUtils.getCurrentScreen();
        return currentScreen instanceof CraftingScreen || currentScreen instanceof InventoryScreen;
    }

    public static void tryMassCrafting() {
        RecipePattern selectedRecipe = RecipeStorage.getInstance().getSelectedRecipe();
        if (!selectedRecipe.isValid()) return;

        ContainerSection playerInventory = EnumSection.InventoryWhole.get();

        InventoryTweaks.makeSureNotHoldingItem(playerInventory);

        InventoryUtil.dropAllMatching(ItemUtil.predicateIDMeta(selectedRecipe.getResult()));// first throw those crafting result

        DCCommonConfig.MassCraftingMode.getEnumValue().create().run();
    }
}
