package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCListCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "list")
                .then(literal("help").executes(ctx -> help(ctx.getSource())))
                .then(makeAutoRepeatCommand())
                .then(build("auto_throw", Registries.ITEM, DCCommonConfig.AutoThrowWhiteList))
                .then(build("cull_block_entity", Registries.BLOCK_ENTITY_TYPE, DCCommonConfig.CullBlockEntityList))
                .then(build("cull_entity", Registries.ENTITY_TYPE, DCCommonConfig.CullEntityList))
                .then(build("cull_particle", Registries.PARTICLE_TYPE, DCCommonConfig.CullParticleList))
                .then(build("highlight", Registries.ENTITY_TYPE, DCCommonConfig.HighlightEntityList))
                .then(build("mute_sound", Registries.SOUND_EVENT, DCCommonConfig.MuteSoundList))
        );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> makeAutoRepeatCommand() {
        List<String> list = DCCommonConfig.AutoRepeatPlayerList.getStrings();
        String argument = "player";
        return of("auto_repeat", list)
                .argumentName(argument)
                .addSuggest(CommandFactory.PLAYER_SUGGESTION)
                .addExecute(ctx -> {
                    FabricClientCommandSource source = ctx.getSource();
                    String player = StringArgumentType.getString(ctx, argument);
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
        return of(name, list)
                .argumentName(registry.getKey().getValue().getPath())
                .addSuggest((ctx, builder) -> CommandSource.suggestIdentifiers(registry.getIds(), builder))
                .build();
    }

    private static Builder of(String name, List<String> list) {
        return new Builder(name, list);
    }

    private static class Builder {
        private final String name;
        private final List<String> list;

        private Command<FabricClientCommandSource> addExecute;

        private String argumentName;
        private SuggestionProvider<FabricClientCommandSource> addSuggest;

        private Builder(String name, List<String> list) {
            this.name = name;
            this.list = list;

            this.addExecute = ctx -> {
                FabricClientCommandSource source = ctx.getSource();
                String key = StringArgumentType.getString(ctx, argumentName);
                if (list.contains(key)) {
                    source.sendFeedback(Text.literal("条目已存在: " + key));
                } else {
                    list.add(key);
                    source.sendFeedback(Text.literal("成功添加到列表: " + key));
                }
                return Command.SINGLE_SUCCESS;
            };
        }

        private Builder addExecute(Command<FabricClientCommandSource> execute) {
            this.addExecute = execute;
            return this;
        }

        private Builder argumentName(String name) {
            this.argumentName = name;
            return this;
        }

        private Builder addSuggest(SuggestionProvider<FabricClientCommandSource> suggest) {
            this.addSuggest = suggest;
            return this;
        }

        private LiteralArgumentBuilder<FabricClientCommandSource> build() {
            if (argumentName == null) throw new IllegalArgumentException();
            if (addSuggest == null) throw new IllegalArgumentException();
            return literal(name)
                    .then(literal("add")
                            .then(makeAdd()))
                    .then(literal("list")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(Text.literal(list.toString()));
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(literal("remove")
                            .then(makeRemove()))
                    .then(literal("remove_all")
                            .executes(ctx -> {
                                ctx.getSource().sendFeedback(Text.literal("成功清空列表: " + list.toString()));
                                list.clear();
                                return Command.SINGLE_SUCCESS;
                            }))
                    ;
        }

        private RequiredArgumentBuilder<FabricClientCommandSource, ?> makeAdd() {
            return argument(argumentName, StringArgumentType.greedyString())
                    .suggests(addSuggest)
                    .executes(addExecute);
        }

        private RequiredArgumentBuilder<FabricClientCommandSource, String> makeRemove() {
            return argument(argumentName, StringArgumentType.greedyString())
                    .suggests((ctx, builder) -> CommandSource.suggestMatching(list, builder))
                    .executes(ctx -> {
                        FabricClientCommandSource source = ctx.getSource();
                        String item = ctx.getArgument(argumentName, String.class);
                        if (list.remove(item)) {
                            source.sendFeedback(Text.literal("成功从列表删除: " + item));
                        } else {
                            source.sendFeedback(Text.literal("该条目不存在: " + item));
                        }
                        return Command.SINGLE_SUCCESS;
                    });
        }
    }
}
