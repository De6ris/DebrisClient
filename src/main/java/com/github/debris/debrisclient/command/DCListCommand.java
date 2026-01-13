package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.config.DCCommonConfig;
import com.github.debris.debrisclient.localization.ListCommandText;
import com.github.debris.debrisclient.util.RayTraceUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCListCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "list")
                .then(literal("help").executes(ctx -> help(ctx.getSource())))
                .then(makeAutoRepeat())
                .then(makeAutoThrow())
                .then(makeCullBlockEntity())
                .then(makeCullEntity())
                .then(build("cull_particle", BuiltInRegistries.PARTICLE_TYPE, DCCommonConfig.CullParticleList))
                .then(build("highlight", BuiltInRegistries.ENTITY_TYPE, DCCommonConfig.HighlightEntityList))
                .then(build("mute_sound", BuiltInRegistries.SOUND_EVENT, DCCommonConfig.MuteSoundList))
        );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeAutoRepeat() {
        List<String> list = DCCommonConfig.AutoRepeatPlayerList.getStrings();
        return of("auto_repeat", list)
                .argumentName("player")
                .addSuggestion(CommandFactory.PLAYER_SUGGESTION)
                .addExecute((source, player) -> {
                    String self = source.getPlayer().getGameProfile().name();
                    if (player.equals(self)) {
                        source.sendFeedback(Component.literal("请勿添加你自己"));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (list.contains(player)) {
                        source.sendFeedback(Component.literal("此玩家已存在: " + player));
                        return Command.SINGLE_SUCCESS;
                    }
                    list.add(player);
                    source.sendFeedback(Component.literal("成功添加此玩家: " + player));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeAutoThrow() {
        return of("auto_throw", BuiltInRegistries.ITEM, DCCommonConfig.AutoThrowWhiteList)
                .fastSuggestion(source -> {
                    ItemStack stack = source.getPlayer().getMainHandItem();
                    return stack.isEmpty() ? Optional.empty() : Optional.of(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                })
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullBlockEntity() {
        return of("cull_block_entity", BuiltInRegistries.BLOCK_ENTITY_TYPE, DCCommonConfig.CullBlockEntityList)
                .fastSuggestion(source -> RayTraceUtil.getRayTraceBlockEntity(source.getClient())
                        .map(blockEntity -> BlockEntityType.getKey(blockEntity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullEntity() {
        return of("cull_entity", BuiltInRegistries.ENTITY_TYPE, DCCommonConfig.CullEntityList)
                .fastSuggestion(source -> RayTraceUtil.getRayTraceEntity(source.getClient())
                        .map(entity -> EntityType.getKey(entity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(ListCommandText.HELP.translate());
        return Command.SINGLE_SUCCESS;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> build(String name, Registry<?> registry, ConfigStringList config) {
        return build(name, registry, config.getStrings());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> build(String name, Registry<?> registry, List<String> list) {
        return of(name, registry, list).build();
    }

    private static Builder of(String name, List<String> list) {
        return new Builder(name, list);
    }

    private static Builder of(String name, Registry<?> registry, ConfigStringList config) {
        return of(name, registry, config.getStrings());
    }

    private static Builder of(String name, Registry<?> registry, List<String> list) {
        return of(name, list)
                .argumentName(registry.key().identifier().getPath())
                .addSuggestion((ctx, builder) ->
                        SharedSuggestionProvider.suggestResource(registry.keySet(), builder));
    }

    private static class Builder {
        private final String name;
        private final List<String> list;
        private String argumentName;

        @Nullable
        private FastSuggestion fastSuggestion;
        private SuggestionProvider<FabricClientCommandSource> addSuggestion;
        private StringExecutor addExecute;
        private StringExecutor removeExecute;

        private Builder(String name, List<String> list) {
            this.name = name;
            this.list = list;

            this.argumentName = "dummy";
            this.addExecute = (source, key) -> {
                if (list.contains(key)) {
                    source.sendFeedback(ListCommandText.ENTRY_EXISTS.translate(key));
                } else {
                    list.add(key);
                    source.sendFeedback(ListCommandText.ADD.translate(key));
                }
                return Command.SINGLE_SUCCESS;
            };
            this.removeExecute = (source, key) -> {
                if (list.remove(key)) {
                    source.sendFeedback(ListCommandText.DELETE.translate(key));
                } else {
                    source.sendFeedback(ListCommandText.ENTRY_NOT_EXIST.translate(key));
                }
                return Command.SINGLE_SUCCESS;
            };
        }

        private Builder addSuggestion(SuggestionProvider<FabricClientCommandSource> suggestion) {
            this.addSuggestion = suggestion;
            return this;
        }

        private Builder fastSuggestion(FastSuggestion suggestion) {
            this.fastSuggestion = suggestion;
            return this;
        }

        private Builder argumentName(String name) {
            this.argumentName = name;
            return this;
        }

        private Builder addExecute(StringExecutor execute) {
            this.addExecute = execute;
            return this;
        }

        private Builder removeExecute(StringExecutor execute) {
            this.removeExecute = execute;
            return this;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> build() {
            LiteralArgumentBuilder<FabricClientCommandSource> builder = literal(name)
                    .then(makeAdd())
                    .then(literal("clear")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(ListCommandText.CLEAR.translate(list.toString()));
                                list.clear();
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(literal("list")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(Component.literal(list.toString()));
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(makeRemove());
            if (this.fastSuggestion != null) {
                builder.then(makeAddThis()).then(makeRemoveThis());
            }
            return builder;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> makeAdd() {
            return literal("add")
                    .then(
                            argument(argumentName, StringArgumentType.greedyString())
                                    .suggests(this.addSuggestion)
                                    .executes(ctx ->
                                            addExecute.run(ctx.getSource(), StringArgumentType.getString(ctx, argumentName))
                                    )
                    );
        }

        @SuppressWarnings("DataFlowIssue")
        private LiteralArgumentBuilder<FabricClientCommandSource> makeAddThis() {
            return literal("add_this")
                    .executes(ctx -> {
                        FabricClientCommandSource source = ctx.getSource();
                        Optional<String> optional = fastSuggestion.provide(source);
                        if (optional.isEmpty()) {
                            source.sendFeedback(ListCommandText.FAIL_DEFAULT_ARGUMENT.translate());
                            return 0;
                        }
                        return addExecute.run(source, optional.get());
                    });
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> makeRemove() {
            return literal("remove")
                    .then(
                            argument(argumentName, StringArgumentType.greedyString())
                                    .suggests(CommandFactory.suggestMatching(list::stream))
                                    .executes(ctx ->
                                            removeExecute.run(ctx.getSource(), StringArgumentType.getString(ctx, argumentName))
                                    )
                    );
        }

        @SuppressWarnings("DataFlowIssue")
        private LiteralArgumentBuilder<FabricClientCommandSource> makeRemoveThis() {
            return literal("remove_this")
                    .executes(ctx -> {
                        FabricClientCommandSource source = ctx.getSource();
                        Optional<String> optional = fastSuggestion.provide(source);
                        if (optional.isEmpty()) {
                            source.sendFeedback(ListCommandText.FAIL_DEFAULT_ARGUMENT.translate());
                            return 0;
                        }
                        return removeExecute.run(source, optional.get());
                    });
        }

    }

    @FunctionalInterface
    private interface StringExecutor {
        int run(FabricClientCommandSource source, String string);
    }

    @FunctionalInterface
    private interface FastSuggestion {
        Optional<String> provide(FabricClientCommandSource source);
    }
}
