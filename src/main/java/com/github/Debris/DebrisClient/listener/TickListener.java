package com.github.Debris.DebrisClient.listener;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.inventory.stoneCutter.StoneCutterUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.MassCraftingApi;
import com.github.Debris.DebrisClient.util.BotUtil;
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
            if (this.hasNewerItemScroller()) {
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
    }

    private boolean hasNewerItemScroller() {
        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer("itemscroller");
        if (optional.isPresent()) {
            Version version = optional.get().getMetadata().getVersion();
            try {
                Version parse = Version.parse("0.24.50");
                if (version.compareTo(parse) >= 0) return true;
            } catch (VersionParsingException e) {
                return false;
            }
        }
        return false;
    }


    public static void onRenderTick(MinecraftClient client) {
        if (DCCommonConfig.AutoGuiQuitting.getBooleanValue()) {
            if (client.currentScreen instanceof HandledScreen<?> guiContainer) {
                guiContainer.close();
            }
        }
    }

}
