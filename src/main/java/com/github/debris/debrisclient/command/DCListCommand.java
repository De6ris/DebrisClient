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
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

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
                .addSuggest(CommandFactory.PLAYER_SUGGESTION)
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
        DefaultedRegistry<Item> registry = BuiltInRegistries.ITEM;
        return of("auto_throw", registry, DCCommonConfig.AutoThrowWhiteList)
                .defaultArgumentProvider(source -> {
                    ItemStack stack = source.getPlayer().getMainHandItem();
                    if (stack.isEmpty()) {
                        return Optional.empty();
                    } else {
                        Item item = stack.getItem();
                        String key = registry.getKey(item).toString();
                        return Optional.of(key);
                    }
                })
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullBlockEntity() {
        return of("cull_block_entity", BuiltInRegistries.BLOCK_ENTITY_TYPE, DCCommonConfig.CullBlockEntityList)
                .defaultArgumentProvider(source -> RayTraceUtil.getRayTraceBlockEntity(source.getClient())
                        .map(blockEntity -> BlockEntityType.getKey(blockEntity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullEntity() {
        return of("cull_entity", BuiltInRegistries.ENTITY_TYPE, DCCommonConfig.CullEntityList)
                .defaultArgumentProvider(source -> RayTraceUtil.getRayTraceEntity(source.getClient())
                        .map(entity -> EntityType.getKey(entity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(ListCommandText.HELP.text());
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
                .addSuggest((ctx, builder) -> SharedSuggestionProvider.suggestResource(registry.keySet(), builder));
    }

    private static class Builder {
        private final String name;
        private final List<String> list;

        /**
         * For calling add and remove without params.
         */
        @Nullable
        private DefaultArgumentProvider defaultArgumentProvider;
        @Nullable
        private SuggestionProvider<FabricClientCommandSource> addSuggest;

        private String argumentName;
        private StringExecutor addExecute;
        private StringExecutor removeExecute;

        private Builder(String name, List<String> list) {
            this.name = name;
            this.list = list;

            this.argumentName = "dummy";
            this.addExecute = (source, key) -> {
                if (list.contains(key)) {
                    source.sendFeedback(ListCommandText.ENTRY_EXISTS.text(key));
                } else {
                    list.add(key);
                    source.sendFeedback(ListCommandText.ADD.text(key));
                }
                return Command.SINGLE_SUCCESS;
            };
            this.removeExecute = (source, key) -> {
                if (list.remove(key)) {
                    source.sendFeedback(ListCommandText.DELETE.text(key));
                } else {
                    source.sendFeedback(ListCommandText.ENTRY_NOT_EXIST.text(key));
                }
                return Command.SINGLE_SUCCESS;
            };
        }

        public Builder defaultArgumentProvider(DefaultArgumentProvider defaultArgumentProvider) {
            this.defaultArgumentProvider = defaultArgumentProvider;
            return this;
        }

        private Builder addSuggest(SuggestionProvider<FabricClientCommandSource> suggest) {
            this.addSuggest = suggest;
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
            return literal(name)
                    .then(makeAdd())
                    .then(literal("clear")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(ListCommandText.CLEAR.text(list.toString()));
                                list.clear();
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(literal("list")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(Component.literal(list.toString()));
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(makeRemove())
                    ;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> makeAdd() {
            LiteralArgumentBuilder<FabricClientCommandSource> builder = literal("add")
                    .then(argument(argumentName, StringArgumentType.greedyString())
                            .suggests(addSuggest)
                            .executes(ctx -> addExecute.run(ctx.getSource(), StringArgumentType.getString(ctx, argumentName)))
                    );
            if (defaultArgumentProvider != null) {
                builder.executes(ctx -> {
                    FabricClientCommandSource source = ctx.getSource();
                    defaultArgumentProvider.provide(source).ifPresentOrElse(
                            key -> addExecute.run(source, key),
                            () -> source.sendFeedback(ListCommandText.FAIL_DEFAULT_ARGUMENT.text())
                    );
                    return Command.SINGLE_SUCCESS;
                });
            }
            return builder;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> makeRemove() {
            LiteralArgumentBuilder<FabricClientCommandSource> x = literal("remove")
                    .then(
                            argument(argumentName, StringArgumentType.greedyString())
                                    .suggests(CommandFactory.suggestMatching(list::stream))
                                    .executes(
                                            ctx -> removeExecute.run(
                                                    ctx.getSource(),
                                                    StringArgumentType.getString(ctx, argumentName)
                                            )
                                    )
                    );
            if (defaultArgumentProvider != null) {
                x.executes(ctx -> {
                    FabricClientCommandSource source = ctx.getSource();
                    defaultArgumentProvider.provide(source).ifPresentOrElse(
                            key -> removeExecute.run(source, key),
                            () -> source.sendFeedback(ListCommandText.FAIL_DEFAULT_ARGUMENT.text())
                    );
                    return Command.SINGLE_SUCCESS;
                });
            }
            return x;
        }
    }

    @FunctionalInterface
    private interface StringExecutor {
        int run(FabricClientCommandSource source, String string);
    }

    @FunctionalInterface
    private interface DefaultArgumentProvider {
        Optional<String> provide(FabricClientCommandSource source);
    }
}
