package com.github.Debris.DebrisClient.mixin.client.gui;

import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookScreen.class)
public interface IMixinRecipeBookScreen {
    @Accessor("recipeBook")
    RecipeBookWidget<?> getRecipeBook();
}
