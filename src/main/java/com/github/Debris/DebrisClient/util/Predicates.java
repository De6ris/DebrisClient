package com.github.Debris.DebrisClient.util;

import fi.dy.masa.malilib.util.GuiUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Predicates {
    public static boolean notInGame(MinecraftClient client) {
        return client.world == null || client.player == null;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean notInGuiContainer(MinecraftClient client) {
        if (notInGame(client)) return true;
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

    public static boolean inGameNoGui(MinecraftClient client) {
        if (notInGame(client)) return false;
        return client.currentScreen == null;
    }

    public static boolean hasMod(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean isContainerBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).hasBlockEntity() && world.getChunk(pos).getBlockEntity(pos) instanceof ScreenHandlerFactory;
    }
}
