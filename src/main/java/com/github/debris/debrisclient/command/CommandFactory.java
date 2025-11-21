package com.github.debris.debrisclient.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class CommandFactory {
    public static final SuggestionProvider<FabricClientCommandSource> PLAYER_SUGGESTION =
            (ctx, builder) ->
                    SharedSuggestionProvider.suggest(getPlayerSuggestions(ctx.getSource()).stream(), builder);

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, ?> ofRegistryKey(
            ResourceKey<Registry<T>> registryKey,
            BiFunction<CommandContext<FabricClientCommandSource>, Holder.Reference<T>, Integer> execution
    ) {
        return ofRegistryKey(registryKey.location().getPath(), registryKey, execution);
    }

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, ?> ofRegistryKey(
            String argument,
            ResourceKey<Registry<T>> registryKey,
            BiFunction<CommandContext<FabricClientCommandSource>, Holder.Reference<T>, Integer> execution
    ) {
        return argument(argument, CResourceKeyArgument.key(registryKey))
                .executes(ctx -> execution.apply(ctx,
                        CResourceKeyArgument.getRegistryEntry(ctx, argument, registryKey,
                                new DynamicCommandExceptionType(
                                        element -> Component.translatableEscape("argument.resource.invalid_type", element, "unknown", registryKey)
                                )))
                );
    }

    public static Collection<String> getPlayerSuggestions(FabricClientCommandSource source) {
        Set<String> players = new LinkedHashSet<>(List.of("Steve", "Alex"));
        players.addAll(source.getOnlinePlayerNames());
        return players;
    }

    /**
     * Requires immutable
     */
    public static SuggestionProvider<FabricClientCommandSource> suggestMatching(Collection<String> candidates) {
        return (
                ctx,
                builder
        ) -> SharedSuggestionProvider.suggest(candidates, builder);
    }

    public static SuggestionProvider<FabricClientCommandSource> suggestMatching(Supplier<Stream<String>> candidates) {
        return (
                ctx,
                builder
        ) -> SharedSuggestionProvider.suggest(candidates.get(), builder);
    }

}
