package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.util.RayTraceUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
                .then(build("cull_particle", Registries.PARTICLE_TYPE, DCCommonConfig.CullParticleList))
                .then(build("highlight", Registries.ENTITY_TYPE, DCCommonConfig.HighlightEntityList))
                .then(build("mute_sound", Registries.SOUND_EVENT, DCCommonConfig.MuteSoundList))
        );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeAutoRepeat() {
        List<String> list = DCCommonConfig.AutoRepeatPlayerList.getStrings();
        return of("auto_repeat", list)
                .argumentName("player")
                .addSuggest(CommandFactory.PLAYER_SUGGESTION)
                .addExecute((source, player) -> {
                    String self = source.getPlayer().getGameProfile().getName();
                    if (player.equals(self)) {
                        source.sendFeedback(Text.literal("请勿添加你自己"));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (list.contains(player)) {
                        source.sendFeedback(Text.literal("此玩家已存在: " + player));
                        return Command.SINGLE_SUCCESS;
                    }
                    list.add(player);
                    source.sendFeedback(Text.literal("成功添加此玩家: " + player));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeAutoThrow() {
        List<String> list = DCCommonConfig.AutoThrowWhiteList.getStrings();
        DefaultedRegistry<Item> registry = Registries.ITEM;
        return of("auto_throw", registry, list)
                .stringSupplier(source -> {
                    ItemStack stack = source.getPlayer().getMainHandStack();
                    if (stack.isEmpty()) {
                        return Optional.empty();
                    } else {
                        Item item = stack.getItem();
                        String key = registry.getId(item).toString();
                        return Optional.of(key);
                    }
                })
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullBlockEntity() {
        List<String> list = DCCommonConfig.CullBlockEntityList.getStrings();
        return of("cull_block_entity", Registries.BLOCK_ENTITY_TYPE, list)
                .stringSupplier(source -> RayTraceUtil.getRayTraceBlockEntity(source.getClient())
                        .map(blockEntity -> BlockEntityType.getId(blockEntity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeCullEntity() {
        List<String> list = DCCommonConfig.CullEntityList.getStrings();
        return of("cull_entity", Registries.ENTITY_TYPE, list)
                .stringSupplier(source -> RayTraceUtil.getRayTraceEntity(source.getClient())
                        .map(entity -> EntityType.getId(entity.getType()))
                        .map(Identifier::toString))
                .build();
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("此命令可对本模组配置的各个列表进行增删改查的操作."));
        source.sendFeedback(Text.literal("你也可以在配置GUI,或配置文件中进行这些操作."));
        source.sendFeedback(Text.literal("此命令方便之处在于能tab补全."));
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

    private static Builder of(String name, Registry<?> registry, List<String> list) {
        return of(name, list)
                .argumentName(registry.getKey().getValue().getPath())
                .addSuggest((ctx, builder) -> CommandSource.suggestIdentifiers(registry.getIds(), builder));
    }

    private static class Builder {
        private final String name;
        private final List<String> list;

        /**
         * For calling add and remove without params.
         */
        @Nullable
        private StringSupplier stringSupplier;
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
                    source.sendFeedback(Text.literal("条目已存在: " + key));
                } else {
                    list.add(key);
                    source.sendFeedback(Text.literal("成功添加到列表: " + key));
                }
                return Command.SINGLE_SUCCESS;
            };
            this.removeExecute = (source, key) -> {
                if (list.remove(key)) {
                    source.sendFeedback(Text.literal("成功从列表删除: " + key));
                } else {
                    source.sendFeedback(Text.literal("该条目不存在: " + key));
                }
                return Command.SINGLE_SUCCESS;
            };
        }

        public Builder stringSupplier(StringSupplier stringSupplier) {
            this.stringSupplier = stringSupplier;
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
                                ctx.getSource().sendFeedback(Text.literal("成功清空列表: " + list.toString()));
                                list.clear();
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(literal("list")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(Text.literal(list.toString()));
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
            if (stringSupplier != null) {
                builder.executes(ctx -> {
                    FabricClientCommandSource source = ctx.getSource();
                    stringSupplier.supply(source).ifPresentOrElse(key -> addExecute.run(source, key), () -> source.sendFeedback(Text.literal("获取默认参数失败")));
                    return Command.SINGLE_SUCCESS;
                });
            }
            return builder;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> makeRemove() {
            LiteralArgumentBuilder<FabricClientCommandSource> x = literal("remove")
                    .then(argument(argumentName, StringArgumentType.greedyString())
                            .suggests((ctx, builder) -> CommandSource.suggestMatching(list, builder))
                            .executes(ctx -> removeExecute.run(ctx.getSource(), StringArgumentType.getString(ctx, argumentName))));
            if (stringSupplier != null) {
                x.executes(ctx -> {
                    FabricClientCommandSource source = ctx.getSource();
                    stringSupplier.supply(source).ifPresentOrElse(key -> removeExecute.run(source, key), () -> source.sendFeedback(Text.literal("获取默认参数失败")));
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
    private interface StringSupplier {
        Optional<String> supply(FabricClientCommandSource source);
    }
}
