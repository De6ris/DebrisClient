package com.github.debris.debrisclient.util;

import com.github.debris.debrisclient.inventory.section.IContainer;
import com.github.debris.debrisclient.mixin.client.IMixinClientRecipeBook;
import com.github.debris.debrisclient.mixin.client.IMixinMinecraftClient;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatHud;
import com.github.debris.debrisclient.mixin.client.gui.IMixinChatScreen;
import com.github.debris.debrisclient.mixin.client.gui.IMixinGuiContainer;
import com.github.debris.debrisclient.mixin.client.gui.IMixinRecipeBookScreen;
import com.github.debris.debrisclient.mixin.client.input.IClientInputMixin;
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
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AccessorUtil {
    public static Slot getHoveredSlot(AbstractContainerScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getHoveredSlot();
    }

    public static int getGuiLeft(AbstractContainerScreen<?> gui) {
        return ((IMixinGuiContainer) gui).dc$getGuiLeft();
    }

    public static List<GuiMessage.Line> getVisibleMessages(ChatComponent chatHud) {
        return ((IMixinChatHud) chatHud).getTrimmedMessages();
    }

    public static void use(Minecraft client) {
        ((IMixinMinecraftClient) client).invokeDoItemUse();
    }

    public static boolean attack(Minecraft client) {
        return ((IMixinMinecraftClient) client).invokeDoAttack();
    }

    public static Map<RecipeDisplayId, RecipeDisplayEntry> getRecipes(ClientRecipeBook recipeBook) {
        return ((IMixinClientRecipeBook) recipeBook).getRecipes();
    }

    public static RecipeBookComponent<?> getRecipeBookWidget(AbstractRecipeBookScreen<?> screen) {
        return ((IMixinRecipeBookScreen) screen).getRecipeBook();
    }

    public static EditBox getChatField(ChatScreen screen) {
        return ((IMixinChatScreen) screen).getChatField();
    }

    public static String getTypeString(AbstractContainerMenu container) {
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

    public static void setMoveVector(ClientInput input, Vec2 vec2) {
        ((IClientInputMixin) input).setMoveVector(vec2);
    }
}
