package com.github.Debris.DebrisClient.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CGameProfileArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static dev.xpple.clientarguments.arguments.CGameProfileArgument.gameProfile;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCAutoRepeatCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "auto_repeat")
                .then(literal("add")
                        .then(argument("filter", gameProfile())
                                .executes(ctx -> add(ctx.getSource(), ctx.getArgument("filter", CGameProfileArgument.Result.class)))))
                .then(literal("list")
                        .executes(ctx -> list(ctx.getSource())))
                .then(literal("remove")
                        .then(literal("all")
                                .executes(ctx -> removeAll(ctx.getSource())))
                        .then(argument("filter", gameProfile())
                                .executes(ctx -> remove(ctx.getSource(), ctx.getArgument("filter", CGameProfileArgument.Result.class))))

                )
        );
    }

    private static final List<String> NAMES = new ArrayList<>();

    private static int add(FabricClientCommandSource source, CGameProfileArgument.Result selector) throws CommandSyntaxException {
        String self = source.getPlayer().getGameProfile().getName();
        List<String> successList = new ArrayList<>();
        for (GameProfile player : selector.getNames(source)) {
            String name = player.getName();
            if (name.equals(self)) {
                source.sendFeedback(Text.literal("Do not track yourself"));
                continue;
            }
            if (NAMES.contains(name)) {
                source.sendFeedback(Text.literal("Already tracked player: " + name));
                continue;
            }
            successList.add(name);
        }
        NAMES.addAll(successList);
        source.sendFeedback(Text.literal("Added players: " + successList));
        return successList.size();
    }


    private static int list(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("Tracking players: " + NAMES));
        return Command.SINGLE_SUCCESS;
    }


    private static int remove(FabricClientCommandSource source, CGameProfileArgument.Result selector) throws CommandSyntaxException {
        List<String> list = selector.getNames(source).stream()
                .map(GameProfile::getName)
                .filter(NAMES::contains)
                .toList();
        NAMES.removeAll(list);
        source.sendFeedback(Text.literal("Removed players: " + list));
        return list.size();
    }

    private static int removeAll(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("Removed players: " + NAMES));
        NAMES.clear();
        return Command.SINGLE_SUCCESS;
    }


    public static Stream<String> streamTrackedPlayers() {
        return NAMES.stream();
    }
}
