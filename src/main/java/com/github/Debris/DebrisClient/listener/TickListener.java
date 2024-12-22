package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.MassCraftingApi;
import com.github.Debris.DebrisClient.util.BotUtil;
import com.github.Debris.DebrisClient.util.StringUtil;
import com.github.Debris.DebrisClient.util.TridentUtil;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.Optional;

public class TickListener implements IClientTickHandler {
    @Override
    public void onClientTick(MinecraftClient minecraftClient) {
        if (DCCommonConfig.CutStoneThenDrop.getKeybind().isKeybindHeld() || DCCommonConfig.StartStoneCutting.getBooleanValue()) {
            if (StoneCutterUtil.isStoneCutterGui()) {
                StoneCutterUtil.cutStoneThenDrop();
            }
        }

        if (DCCommonConfig.MyMassCrafting.getKeybind().isKeybindHeld() || DCCommonConfig.StartMassCrafting.getBooleanValue()) {
            if (StringUtil.isModLoadedWithNewEnoughVersion("itemscroller", "0.24.50")) {
                if (MassCraftingApi.isCraftingGui()) {
                    MassCraftingApi.tryMassCrafting();
                }
            }
        }

        if (DCCommonConfig.KickBot.getKeybind().isKeybindHeld()) {
            if (FabricLoader.getInstance().isModLoaded("tweakeroo")) {
                BotUtil.tryKickBot(minecraftClient);
            }
        }

        TridentUtil.onClientTick(minecraftClient);
    }


    public static void onRenderTick(MinecraftClient client) {
        if (DCCommonConfig.AutoGuiQuitting.getBooleanValue()) {
            if (client.currentScreen instanceof HandledScreen<?> guiContainer) {
                guiContainer.close();
            }
        }
    }

}
