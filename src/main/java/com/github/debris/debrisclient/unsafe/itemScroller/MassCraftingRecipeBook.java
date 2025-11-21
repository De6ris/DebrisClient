package com.github.debris.debrisclient.unsafe.itemScroller;

import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.slf4j.Logger;

import java.util.Optional;

public class MassCraftingRecipeBook extends AbstractMassCrafting {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ContextMap recipeDisplayContext;
    private final ClientRecipeBook recipeBook;
    private final AbstractCraftingMenu craftingContainer;
    private final RecipeBookComponent<?> recipeBookWidget;

    @SuppressWarnings("ConstantConditions")
    public MassCraftingRecipeBook() {
        super();
        Minecraft client = Minecraft.getInstance();
        this.recipeDisplayContext = SlotDisplayContext.fromLevel(client.level);
        this.recipeBook = client.player.getRecipeBook();
        this.craftingContainer = ((AbstractCraftingMenu) InventoryUtil.getContainer(this.currentScreen));
        this.recipeBookWidget = AccessorUtil.getRecipeBookWidget(this.currentScreen);
    }

    @Override
    public void run() {
        Optional<RecipeDisplayId> optional = findVanillaRecipe(this.selectedRecipe);

        if (optional.isEmpty()) return;

        RecipeDisplayId networkRecipeId = optional.get();

        if (!checkResultSlot()) {// if no result, click once
            InventoryUtil.clickRecipe(networkRecipeId, true);// just send packet, slot moving is done by the server
        }

        while (checkResultSlot()) {// while correct result
            dropAllProduct();
            tryTakeResultSlot();
            InventoryUtil.clickRecipe(networkRecipeId, true);
        }

    }

    private Optional<RecipeDisplayId> findVanillaRecipe(RecipePattern recipe) {
        if (this.recipeBookWidget.isVisible()) {
            return findVanillaRecipeByBook(recipe);
        }
        return findVanillaRecipeManual(recipe);
    }

    private Optional<RecipeDisplayId> findVanillaRecipeByBook(RecipePattern recipe) {
        return this.recipeBook.getCollection(SearchRecipeBookCategory.CRAFTING)
                .stream()
                .flatMap(x -> x.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE).stream())
                .filter(x -> x.resultItems(recipeDisplayContext).stream().anyMatch(ItemUtil.predicateID(recipe.getResult())))
                .findFirst()
                .map(RecipeDisplayEntry::id);
    }

    private Optional<RecipeDisplayId> findVanillaRecipeManual(RecipePattern recipe) {
        StackedItemContents recipeFinder = new StackedItemContents();
        InventoryUtil.getPlayerInventory().fillStackedContents(recipeFinder);
        this.craftingContainer.fillCraftSlotsStackedContents(recipeFinder);
        return AccessorUtil.getRecipes(this.recipeBook)
                .values()
                .stream()
                .filter(x -> isCraftingCategory(x.category()))
                .filter(x -> x.resultItems(recipeDisplayContext).stream().anyMatch(ItemUtil.predicateID(recipe.getResult())))
                .filter(x -> x.canCraft(recipeFinder))
                .findFirst()
                .map(RecipeDisplayEntry::id);
    }

    private static boolean isCraftingCategory(RecipeBookCategory category) {
        return SearchRecipeBookCategory.CRAFTING.includedCategories().contains(category);
    }
}
