package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.InteractionUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.xpple.clientarguments.arguments.CEntityArgument;
import dev.xpple.clientarguments.arguments.CGameProfileArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * Same to vanilla spectate command, but no permission required.
 */
public class DCSpectateCommand {
    private static final SimpleCommandExceptionType SPECTATE_SELF_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.spectate.self"));
    private static final DynamicCommandExceptionType NOT_SPECTATOR_EXCEPTION = new DynamicCommandExceptionType(
            playerName -> Component.translatableEscape("commands.spectate.not_spectator", playerName)
    );

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "spectate")
                .then(argument("filter", CGameProfileArgument.gameProfile(true))
                        .executes(ctx -> processCommand(ctx.getSource(), ctx.getArgument("filter", CGameProfileArgument.Result.class))))
        );
    }

    private static int processCommand(FabricClientCommandSource source, CGameProfileArgument.Result filter) throws CommandSyntaxException {
        LocalPlayer self = source.getPlayer();
        GameProfile targetGameProfile = filter.getNames(source).stream().findFirst().orElseThrow(CEntityArgument.PLAYER_NOT_FOUND_EXCEPTION::create);
        if (self.getGameProfile().name().equals(targetGameProfile.name())) {
            throw SPECTATE_SELF_EXCEPTION.create();
        }

        if (!self.isSpectator()) {
            throw NOT_SPECTATOR_EXCEPTION.create(self.getDisplayName());
        }

        Minecraft client = source.getClient();
        InteractionUtil.spectatorTeleport(client, targetGameProfile.id());

        Player targetPlayerEntity = source.getWorld().getPlayerByUUID(targetGameProfile.id());
        if (targetPlayerEntity == null) {
            source.sendFeedback(Component.literal("未在附近找到该玩家实体, 已请求向该玩家传送, 请在传送完成后重试指令"));
        } else {
            InteractionUtil.attackEntity(client, targetPlayerEntity);// the server will set your camera
        }

        return Command.SINGLE_SUCCESS;
    }
}
