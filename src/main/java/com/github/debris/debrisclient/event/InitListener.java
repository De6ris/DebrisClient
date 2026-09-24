package com.github.debris.debrisclient.event;

import com.github.debris.debrisclient.config.Callbacks;
import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.config.InventoryConfig;
import com.github.debris.debrisclient.gui.UniversalSearchScreen;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import net.minecraft.client.Minecraft;

public class InitListener implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(DCCommonConfig.ID.toString(), DCCommonConfig.getInstance());
        ConfigManager.getInstance().registerConfigHandler(InventoryConfig.ID.toString(), InventoryConfig.getInstance());

        InputEventHandler.getKeybindManager().registerKeybindProvider(InputListener.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputListener.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputListener.getInstance());
        Callbacks.init(Minecraft.getInstance());
        TickHandler.getInstance().registerClientTickHandler(new TickListener());
        WorldLoadHandler.getInstance().registerWorldLoadPreHandler(WorldLoadListener.getInstance());
        WorldLoadHandler.getInstance().registerWorldLoadPostHandler(WorldLoadListener.getInstance());
        RenderEventHandler.getInstance().registerWorldLastRenderer(RenderListener.getInstance());

        Registry.CONFIG_SCREEN.registerConfigScreenFactory(DCCommonConfig.MOD_INFO);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(InventoryConfig.MOD_INFO);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(UniversalSearchScreen.Instance);
    }
}
