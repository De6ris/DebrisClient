package com.github.Debris.DebrisClient.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class CommandFactory {
    public static final SuggestionProvider<FabricClientCommandSource> PLAYER_SUGGESTION = (ctx, builder) -> CommandSource.suggestMatching(getPlayerSuggestions(ctx.getSource()), builder);

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, ?> ofRegistryKey(
            RegistryKey<Registry<T>> registryKey,
            BiFunction<CommandContext<FabricClientCommandSource>, RegistryEntry.Reference<T>, Integer> execution
    ) {
        return ofRegistryKey(registryKey.getValue().getPath(), registryKey, execution);
    }

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, ?> ofRegistryKey(
            String argument,
            RegistryKey<Registry<T>> registryKey,
            BiFunction<CommandContext<FabricClientCommandSource>, RegistryEntry.Reference<T>, Integer> execution
    ) {
        return argument(argument, CResourceKeyArgument.key(registryKey))
                .executes(ctx -> execution.apply(ctx,
                        CResourceKeyArgument.getRegistryEntry(ctx, argument, registryKey,
                                new DynamicCommandExceptionType(
                                        element -> Text.stringifiedTranslatable("argument.resource.invalid_type", element, "", registryKey)
                                )))
                );
    }

    public static Collection<String> getPlayerSuggestions(FabricClientCommandSource source) {
        Set<String> players = new LinkedHashSet<>(List.of("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }
}
