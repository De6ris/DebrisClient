package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.util.InteractionUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.xpple.clientarguments.arguments.CEntitySelector;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import static dev.xpple.clientarguments.arguments.CEntityArgument.player;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Same to vanilla spectate command, but no permission required. The target must be within client world though.
 */
public class DCSpectateCommand {
    private static final SimpleCommandExceptionType SPECTATE_SELF_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.spectate.self"));
    private static final DynamicCommandExceptionType NOT_SPECTATOR_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Text.stringifiedTranslatable("commands.spectate.not_spectator", playerName)
    );

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "spectate")
                .then(argument("filter", player())
                        .executes(ctx -> processCommand(ctx.getSource(), ctx.getArgument("filter", CEntitySelector.class))))
        );
    }

    private static int processCommand(FabricClientCommandSource source, CEntitySelector filter) throws CommandSyntaxException {
        ClientPlayerEntity player = source.getPlayer();
        AbstractClientPlayerEntity target = filter.findSinglePlayer(source);
        if (player == target) {
            throw SPECTATE_SELF_EXCEPTION.create();
        }
        if (!player.isSpectator()) {
            throw NOT_SPECTATOR_EXCEPTION.create(player.getDisplayName());
        }
        MinecraftClient client = source.getClient();
        if (!player.canInteractWithEntityIn(target.getBoundingBox(), 3.0D)) {
            InteractionUtil.spectatorTeleport(client, target.getUuid());
        }
        InteractionUtil.attackEntity(client, target);
        return Command.SINGLE_SUCCESS;
    }

}
