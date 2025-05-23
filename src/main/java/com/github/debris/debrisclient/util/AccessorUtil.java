package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.inventory.section.IContainer;
import com.github.debris.debrisclient.mixin.client.IMixinClientRecipeBook;
import com.github.debris.debrisclient.mixin.client.IMixinMinecraftClient;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatHud;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatScreen;
import com.github.debris.debrisclient.mixin.client.gui.IMixinGuiContainer;
import com.github.debris.debrisclient.mixin.client.gui.IMixinRecipeBookScreen;
import com.github.debris.debrisclient.mixin.compat.malilib.IMixinButtonBase;
import com.github.debris.debrisclient.mixin.compat.malilib.IMixinGuiBase;
import com.github.debris.debrisclient.mixin.compat.malilib.IMixinWidgetConfigOption;
import com.github.debris.debrisclient.mixin.compat.malilib.IMixinWidgetListBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
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
import org.jetbrains.annotations.Nullable;

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

    public static List<ButtonBase> getButtons(GuiBase gui) {
        return ((IMixinGuiBase) gui).getButtons();
    }

    public static WidgetBase getHoveredWidget(GuiBase gui) {
        return ((IMixinGuiBase) gui).getHoveredWidget();
    }

    @Nullable
    public static IButtonActionListener getActionListener(ButtonBase button) {
        return ((IMixinButtonBase) button).getActionListener();
    }

    public static String getDisplayString(ButtonBase button) {
        return ((IMixinButtonBase) button).getDisplayString();
    }

    public static GuiConfigsBase.ConfigOptionWrapper getWrapper(WidgetConfigOption widget) {
        return ((IMixinWidgetConfigOption) widget).getWrapper();
    }

    public static void setAllowKeyboardNavigation(WidgetListBase<?, ?> widget, boolean allow) {
        ((IMixinWidgetListBase) widget).setAllowKeyboardNavigation(true);
    }

}
