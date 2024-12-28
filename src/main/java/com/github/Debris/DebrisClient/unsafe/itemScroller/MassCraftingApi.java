package com.github.Debris.DebrisClient.unsafe.itemScroller;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.section.ContainerSection;
import com.github.Debris.DebrisClient.inventory.section.EnumSection;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.inventory.util.ItemUtil;
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

        ((MassCraftingImpl) DCCommonConfig.MassCraftingMode.getOptionListValue()).create().run();
    }
}
