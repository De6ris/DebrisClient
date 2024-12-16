package com.github.Debris.DebrisClient.unsafe.itemScroller;

import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.input.RecipeInput;
import org.slf4j.Logger;

public class MassCraftingRecipeBook extends AbstractMassCrafting {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void run() {
        ClientWorld world = MinecraftClient.getInstance().world;

        Recipe<RecipeInput> vanillaRecipe = selectedRecipe.lookupVanillaRecipe(world);

        if (vanillaRecipe == null) {
            LOGGER.warn("using recipe book, but fail to look up vanilla recipe");
            return;
        }

        NetworkRecipeId networkRecipeId = selectedRecipe.getNetworkRecipeId();

        if (!checkResultSlot()) {// if no result, click once
            InventoryUtil.clickRecipe(networkRecipeId, true);// just send packet, slot moving is done by the server
        }

        while (checkResultSlot()) {// while correct result
            dropAllProduct();
            tryTakeResultSlot();
            InventoryUtil.clickRecipe(networkRecipeId, true);
        }

    }
}
