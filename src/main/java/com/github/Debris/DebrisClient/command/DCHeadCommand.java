package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.util.ChatUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCHeadCommand {
    private static final DynamicCommandExceptionType INVALID_TYPE_EXCEPTION = new DynamicCommandExceptionType(
            element -> Text.stringifiedTranslatable("argument.resource.invalid_type", element, "", RegistryKeys.SOUND_EVENT)
    );

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "head")
                .then(literal("help").
                        executes(ctx -> help(ctx.getSource())))
                .then(argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> CommandSource.suggestMatching(getPlayerSuggestions(ctx.getSource()), builder))
                        .then(argument("sound_type", CResourceKeyArgument.key(RegistryKeys.SOUND_EVENT))
                                .executes(ctx ->
                                        giveHead(ctx.getSource(), ctx.getArgument("name", String.class),
                                                CResourceKeyArgument.getRegistryEntry(ctx, "sound_type", RegistryKeys.SOUND_EVENT, INVALID_TYPE_EXCEPTION))))));
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("用以创建使音符盒拥有特殊音效的玩家头颅"));
        return Command.SINGLE_SUCCESS;
    }

    private static Collection<String> getPlayerSuggestions(FabricClientCommandSource source) {
        Set<String> players = new LinkedHashSet<>(List.of("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }

    private static int giveHead(FabricClientCommandSource source, String name, RegistryEntry.Reference<SoundEvent> sound) {
        String soundKey = sound.registryKey().getValue().toString();
        String command = String.format("/give @s minecraft:player_head[minecraft:profile=\"%s\", minecraft:note_block_sound=\"%s\"]", name, soundKey);
        ChatUtil.sendChat(source.getClient(), command);
        return Command.SINGLE_SUCCESS;
    }
}
