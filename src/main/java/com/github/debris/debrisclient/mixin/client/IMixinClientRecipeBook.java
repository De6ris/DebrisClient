package com.github.debris.debrisclient.mixin.client;

import net.minecraft.client.ClientRecipeBook;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientRecipeBook.class)
public interface IMixinClientRecipeBook {
    @Accessor("known")
    Map<RecipeDisplayId, RecipeDisplayEntry> getRecipes();
}
