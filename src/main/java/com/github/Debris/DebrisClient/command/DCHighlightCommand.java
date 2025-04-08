package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCHighlightCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "highlight")
                .then(CommandFactory.ofRegistryKey("entity_type", RegistryKeys.ENTITY_TYPE, (ctx, reference) -> {
                    String key = reference.registryKey().getValue().toString();
                    DCCommonConfig.HighlightEntityList.getStrings().add(key);
                    ctx.getSource().sendFeedback(Text.literal("已添加到列表: " + key));
                    return Command.SINGLE_SUCCESS;
                }))
        );
    }
}
