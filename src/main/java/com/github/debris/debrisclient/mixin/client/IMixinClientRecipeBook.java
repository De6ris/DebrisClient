package com.github.debris.debrisclient.mixin.client;

import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientRecipeBook.class)
public interface IMixinClientRecipeBook {
    @Accessor("recipes")
    Map<NetworkRecipeId, RecipeDisplayEntry> getRecipes();
}
