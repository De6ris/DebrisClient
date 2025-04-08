package com.github.Debris.DebrisClient.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class CommandFactory {
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
}
