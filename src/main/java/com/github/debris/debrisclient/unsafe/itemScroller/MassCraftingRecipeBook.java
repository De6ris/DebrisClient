package com.github.debris.debrisclient.unsafe.itemScroller;

import com.github.debris.debrisclient.util.AccessorUtil;
import com.github.debris.debrisclient.util.InventoryUtil;
import com.github.debris.debrisclient.util.ItemUtil;
import com.mojang.logging.LogUtils;
import fi.dy.masa.itemscroller.recipes.RecipePattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.util.context.ContextParameterMap;
import org.slf4j.Logger;

import java.util.Optional;

public class MassCraftingRecipeBook extends AbstractMassCrafting {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ContextParameterMap recipeDisplayContext;
    private final ClientRecipeBook recipeBook;
    private final AbstractCraftingScreenHandler craftingContainer;
    private final RecipeBookWidget<?> recipeBookWidget;

    @SuppressWarnings("ConstantConditions")
    public MassCraftingRecipeBook() {
        super();
        MinecraftClient client = MinecraftClient.getInstance();
        this.recipeDisplayContext = SlotDisplayContexts.createParameters(client.world);
        this.recipeBook = client.player.getRecipeBook();
        this.craftingContainer = ((AbstractCraftingScreenHandler) InventoryUtil.getContainer(this.currentScreen));
        this.recipeBookWidget = AccessorUtil.getRecipeBookWidget(this.currentScreen);
    }

    @Override
    public void run() {
        Optional<NetworkRecipeId> optional = findVanillaRecipe(this.selectedRecipe);

        if (optional.isEmpty()) return;

        NetworkRecipeId networkRecipeId = optional.get();

        if (!checkResultSlot()) {// if no result, click once
            InventoryUtil.clickRecipe(networkRecipeId, true);// just send packet, slot moving is done by the server
        }

        while (checkResultSlot()) {// while correct result
            dropAllProduct();
            tryTakeResultSlot();
            InventoryUtil.clickRecipe(networkRecipeId, true);
        }

    }

    private Optional<NetworkRecipeId> findVanillaRecipe(RecipePattern recipe) {
        if (this.recipeBookWidget.isOpen()) {
            return findVanillaRecipeByBook(recipe);
        }
        return findVanillaRecipeManual(recipe);
    }

    private Optional<NetworkRecipeId> findVanillaRecipeByBook(RecipePattern recipe) {
        return this.recipeBook.getResultsForCategory(RecipeBookType.CRAFTING)
                .stream()
                .flatMap(x -> x.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE).stream())
                .filter(x -> x.getStacks(recipeDisplayContext).stream().anyMatch(ItemUtil.predicateID(recipe.getResult())))
                .findFirst()
                .map(RecipeDisplayEntry::id);
    }

    private Optional<NetworkRecipeId> findVanillaRecipeManual(RecipePattern recipe) {
        RecipeFinder recipeFinder = new RecipeFinder();
        InventoryUtil.getPlayerInventory().populateRecipeFinder(recipeFinder);
        this.craftingContainer.populateRecipeFinder(recipeFinder);
        return AccessorUtil.getRecipes(this.recipeBook)
                .values()
                .stream()
                .filter(x -> isCraftingCategory(x.category()))
                .filter(x -> x.getStacks(recipeDisplayContext).stream().anyMatch(ItemUtil.predicateID(recipe.getResult())))
                .filter(x -> x.isCraftable(recipeFinder))
                .findFirst()
                .map(RecipeDisplayEntry::id);
    }

    private static boolean isCraftingCategory(RecipeBookCategory category) {
        return RecipeBookType.CRAFTING.getCategories().contains(category);
    }
}
