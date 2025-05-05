package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.inventory.section.IContainer;
import com.github.debris.debrisclient.mixin.client.IMixinClientRecipeBook;
import com.github.debris.debrisclient.mixin.client.IMixinMinecraftClient;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatHud;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatScreen;
import com.github.debris.debrisclient.mixin.client.gui.IMixinGuiContainer;
import com.github.debris.debrisclient.mixin.client.gui.IMixinRecipeBookScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;
import java.util.Map;

public class AccessorUtil {
    public static Slot getHoveredSlot(HandledScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getHoveredSlot();
    }

    public static int getGuiLeft(HandledScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getGuiLeft();
    }

    public static List<ChatHudLine.Visible> getVisibleMessages(ChatHud chatHud) {
        return ((IMixinChatHud) chatHud).getVisibleMessages();
    }

    public static void use(MinecraftClient client) {
        ((IMixinMinecraftClient) client).invokeDoItemUse();
    }

    public static boolean attack(MinecraftClient client) {
        return ((IMixinMinecraftClient) client).invokeDoAttack();
    }

    public static Map<NetworkRecipeId, RecipeDisplayEntry> getRecipes(ClientRecipeBook recipeBook) {
        return ((IMixinClientRecipeBook) recipeBook).getRecipes();
    }

    public static RecipeBookWidget<?> getRecipeBookWidget(RecipeBookScreen<?> screen) {
        return ((IMixinRecipeBookScreen) screen).getRecipeBook();
    }

    public static TextFieldWidget getChatField(ChatScreen screen) {
        return ((IMixinChatScreen) screen).getChatField();
    }

    public static String getTypeString(ScreenHandler container) {
        return ((IContainer) container).dc$getTypeString();
    }
}
