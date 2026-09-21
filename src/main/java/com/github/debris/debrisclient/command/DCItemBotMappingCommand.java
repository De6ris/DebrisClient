package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.ItemBotMapping;
import com.github.debris.debrisclient.localization.GeneralText;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.ItemStack;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class DCItemBotMappingCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "item_bot_mapping")
                        .then(
                                literal("add")
                                        .then(
                                                argument("key", StringArgumentType.string())
                                                        .suggests((ctx, builder) -> {
                                                            ItemStack stack = ctx.getSource().getPlayer().getMainHandItem();
                                                            return SharedSuggestionProvider.suggest(ItemBotMapping.suggestKey(stack), builder);
                                                        }).then(
                                                                argument("name", StringArgumentType.string())
                                                                        .suggests((ctx, builder) -> {
                                                                            String string = StringArgumentType.getString(ctx, "key");
                                                                            return SharedSuggestionProvider.suggest(ItemBotMapping.suggestName(string), builder);
                                                                        }).executes(
                                                                                ctx -> add(
                                                                                        ctx.getSource(),
                                                                                        StringArgumentType.getString(ctx, "key"),
                                                                                        StringArgumentType.getString(ctx, "name")
                                                                                )
                                                                        )
                                                        )
                                        )


                        )
        );
    }

    private static int add(FabricClientCommandSource source, String key, String name) {
        ItemBotMapping.add(key, name);
        source.sendFeedback(GeneralText.OPERATION_SUCCESS.translate());
        return Command.SINGLE_SUCCESS;
    }
}
