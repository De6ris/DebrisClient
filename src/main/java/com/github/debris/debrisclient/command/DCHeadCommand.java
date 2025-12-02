package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.ChatUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCHeadCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "head")
                        .then(
                                literal("help")
                                        .executes(ctx -> help(ctx.getSource()))
                        )
                        .then(
                                argument("name", StringArgumentType.word())
                                        .suggests(CommandFactory.PLAYER_SUGGESTION)
                                        .then(
                                                CommandFactory.ofRegistryKey(
                                                        Registries.SOUND_EVENT,
                                                        (ctx, entry)
                                                                -> giveHead(
                                                                ctx.getSource(),
                                                                StringArgumentType.getString(ctx, "name"),
                                                                entry
                                                        )
                                                )
                                        )
                        )
        );
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Component.literal("用以创建使音符盒拥有特殊音效的玩家头颅"));
        return Command.SINGLE_SUCCESS;
    }

    private static int giveHead(FabricClientCommandSource source, String name, Holder.Reference<SoundEvent> sound) {
        String soundKey = sound.key().identifier().toString();
        String command = String.format("/give @s minecraft:player_head[minecraft:profile=\"%s\", minecraft:note_block_sound=\"%s\"]", name, soundKey);
        ChatUtil.sendChat(source.getClient(), command);
        return Command.SINGLE_SUCCESS;
    }
}
