package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.config.early.DCEarlyConfig;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterRecipeStorage;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.unsafe.mgButtons.MGButtonReloader;
import com.github.Debris.DebrisClient.util.BotUtil;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class Callbacks {
    public static void init(MinecraftClient client) {
        DCCommonConfig.OpenWindow.getKeybind().setCallback((action, key) -> {
            client.setScreen(new DCConfigUi());
            return true;
        });

        DCCommonConfig.ReloadCommandButton.getKeybind().setCallback((action, key) -> {
            if (FabricLoader.getInstance().isModLoaded("mgbuttons-1_21")) {
                MGButtonReloader.reload();
                InfoUtils.showInGameMessage(Message.MessageType.SUCCESS, "命令按钮: 重载成功");
                return true;
            }
            return false;
        });

        DCCommonConfig.SortItem.getKeybind().setCallback((action, key) -> {
            if (nonContainerEnvironment(client)) return false;
            playClickSound(client);
            return InventoryTweaks.trySort();// this will block other click consumers
        });

        DCCommonConfig.StoreStoneCutterRecipe.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterRecipeViewOpen() && StoneCutterUtil.isOverStoneCutterResult()) {
                StoneCutterRecipeStorage.getInstance().storeRecipe();
                return true;
            }
            return false;
        });

        DCCommonConfig.CutStone.getKeybind().setCallback((action, key) -> {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStone();
                return true;
            }
            return false;
        });

        DCCommonConfig.CarpetSinglePlayerFix.setValueChangeCallback(config -> DCEarlyConfig.getInstance().CarpetFix = config.getBooleanValue());

        DCCommonConfig.RestoreKicking.getKeybind().setCallback((action, key) -> {
            if (BotUtil.restoreKicking(client)) {
                return true;
            }
            return false;
        });

        DCCommonConfig.TEST.getKeybind().setCallback((action, key) -> {
            return false;
        });

        DCCommonConfig.TEST2.getKeybind().setCallback((action, key) -> {
            return false;
        });

    }

    public static boolean nonContainerEnvironment(MinecraftClient client) {
        if (client.world == null) return true;
        if (client.player == null) return true;
        if (client.player.isSpectator()) return true;
        Screen currentScreen = GuiUtils.getCurrentScreen();
        if (currentScreen instanceof HandledScreen<?>) {// container screen
            if (currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen && !creativeInventoryScreen.isInventoryTabSelected()) {
                return true;
            }
            return false;
        }
        return true;
    }

    private static void playClickSound(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
