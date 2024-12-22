package com.github.Debris.DebrisClient.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class Commands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess context) {
        DCAutoRepeatCommand.register(dispatcher);
    }
}
