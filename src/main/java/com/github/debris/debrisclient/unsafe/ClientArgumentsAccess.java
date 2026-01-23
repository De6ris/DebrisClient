package com.github.debris.debrisclient.unsafe;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.function.BiFunction;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class ClientArgumentsAccess {
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
}
