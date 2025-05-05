package com.github.debris.debrisclient.listener;

import com.github.debris.debrisclient.inventory.cutstone.StoneCutterRecipeStorage;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class WorldLoadListener implements IWorldLoadListener {
    private static final WorldLoadListener INSTANCE = new WorldLoadListener();

    public static WorldLoadListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void onWorldLoadPre(ClientWorld worldBefore, ClientWorld worldAfter, MinecraftClient mc) {
        // Quitting to main menu, save the settings before the integrated server gets shut down
        if (worldBefore != null && worldAfter == null) {
            StoneCutterRecipeStorage.getInstance().write(worldBefore.getRegistryManager());
        }
    }

    @Override
    public void onWorldLoadPost(ClientWorld worldBefore, ClientWorld worldAfter, MinecraftClient mc) {
        if (worldAfter == null) StoneCutterRecipeStorage.getInstance().clearAll();

        // Logging in to a world, load the data
        if (worldBefore == null && worldAfter != null) {
            StoneCutterRecipeStorage.getInstance().read(worldAfter.getRegistryManager());
        }

        // Logging out
//        if (worldAfter == null) {
//            ClickPacketBuffer.reset();
//        }
    }
}
