package com.github.Debris.DebrisClient.config;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterRecipeStorage;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.inventory.util.InventoryTweaks;
import com.github.Debris.DebrisClient.unsafe.mgButtons.MGButtonReloader;
import com.github.Debris.DebrisClient.util.BotUtil;
import com.github.Debris.DebrisClient.util.ChatUtil;
import com.github.Debris.DebrisClient.util.MiscUtil;
import com.github.Debris.DebrisClient.util.Predicates;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class Callbacks {
    public static void init(MinecraftClient client) {
        DCCommonConfig.OpenWindow.getKeybind().setCallback((action, key) -> {
            client.setScreen(new DCConfigUi());
            return true;
        });

        DCCommonConfig.ReloadCommandButton.getKeybind().setCallback((action, key) -> {
            if (FabricLoader.getInstance().isModLoaded(ModReference.CommandButton)) {
                MGButtonReloader.reload();
                InfoUtils.showInGameMessage(Message.MessageType.SUCCESS, "命令按钮: 重载成功");
                return true;
            }
            return false;
        });

        DCCommonConfig.SortItem.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
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

        DCCommonConfig.ThrowSection.getKeybind().setCallback((action, key) -> {
            if (Predicates.notInGuiContainer(client)) return false;
            return InventoryTweaks.tryThrowSection();
        });

        DCCommonConfig.RestoreKicking.getKeybind().setCallback((action, key) -> BotUtil.restoreKicking(client));

        DCCommonConfig.BotSpawnCommand.getKeybind().setCallback((action, key) -> BotUtil.suggestBotSpawnCommand(client));

        DCCommonConfig.ResendLastChat.getKeybind().setCallback((action, key) -> ChatUtil.resendLast(client));

        DCCommonConfig.RepeatNewestChat.getKeybind().setCallback((action, key) -> ChatUtil.repeatNewestChat(client));

        DCCommonConfig.AlignWithEnderEye.getKeybind().setCallback((action, key) -> MiscUtil.alignWithEnderEye(client));

        DCCommonConfig.TEST.getKeybind().setCallback((action, key) -> {
            return false;
        });

    }

    private static void playClickSound(MinecraftClient client) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
