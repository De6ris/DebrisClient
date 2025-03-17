package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.xpple.clientarguments.arguments.CResourceKeyArgument;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCCullCommand {
    private static final Function<RegistryKey<?>, DynamicCommandExceptionType> INVALID_TYPE_EXCEPTION_FACTORY =
            type -> new DynamicCommandExceptionType(
                    element -> Text.stringifiedTranslatable("argument.resource.invalid_type", element, "", type)
            );

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "cull")
                .then(literal("help")
                        .executes(ctx -> help(ctx.getSource())))
                .then(literal("entity")
                        .then(argument("entity_type", CResourceKeyArgument.key(RegistryKeys.ENTITY_TYPE))
                                .executes(ctx -> addType(ctx.getSource(),
                                        CResourceKeyArgument.getRegistryEntry(ctx, "entity_type", RegistryKeys.ENTITY_TYPE,
                                                INVALID_TYPE_EXCEPTION_FACTORY.apply(RegistryKeys.ENTITY_TYPE)),
                                        key -> DCCommonConfig.CullEntityList.getStrings().add(key)
                                ))))
                .then(literal("particle")
                        .then(argument("particle_type", CResourceKeyArgument.key(RegistryKeys.PARTICLE_TYPE))
                                .executes(ctx -> addType(ctx.getSource(),
                                        CResourceKeyArgument.getRegistryEntry(ctx, "particle_type", RegistryKeys.PARTICLE_TYPE,
                                                INVALID_TYPE_EXCEPTION_FACTORY.apply(RegistryKeys.PARTICLE_TYPE)),
                                        key -> DCCommonConfig.CullEntityList.getStrings().add(key)
                                ))))
                .then(literal("sound")
                        .then(argument("sound_type", CResourceKeyArgument.key(RegistryKeys.SOUND_EVENT))
                                .executes(ctx -> addType(ctx.getSource(),
                                        CResourceKeyArgument.getRegistryEntry(ctx, "sound_type", RegistryKeys.SOUND_EVENT,
                                                INVALID_TYPE_EXCEPTION_FACTORY.apply(RegistryKeys.SOUND_EVENT)),
                                        key -> DCCommonConfig.MuteSoundList.getStrings().add(key)
                                ))))

        );
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("此命令可将指定类型的实体, 粒子, 音效屏蔽."));
        source.sendFeedback(Text.literal("被添加的类型将储存到本模组配置文件中."));
        source.sendFeedback(Text.literal("可在配置GUI中查看, 和增删操作."));
        source.sendFeedback(Text.literal("此命令方便之处在于能tab补全."));
        return Command.SINGLE_SUCCESS;
    }

    private static int addType(FabricClientCommandSource source, RegistryEntry.Reference<?> type, Consumer<String> keyReader) {
        String key = type.registryKey().getValue().toString();
        keyReader.accept(key);
        source.sendFeedback(Text.literal("已添加到列表: " + key));
        return Command.SINGLE_SUCCESS;
    }
}
