package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.log.GameLog;
import com.github.debris.debrisclient.feat.log.GameLogs;
import com.github.debris.debrisclient.localization.LogCommandText;
import com.github.debris.debrisclient.util.TextFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.text.*;

import java.util.Collection;
import java.util.Collections;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCLogCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "log")
                        .executes(ctx -> list(ctx.getSource()))
                        .then(
                                literal("clear")
                                        .executes(ctx -> clear(ctx.getSource()))
                        )
                        .then(
                                argument("category", StringArgumentType.word())
                                        .suggests(CommandFactory.suggestMatching(GameLogs.getCategories()))
                                        .executes(
                                                ctx -> toggle(
                                                        ctx.getSource(),
                                                        category(ctx)
                                                )
                                        )
                                        .then(
                                                literal("clear")
                                                        .executes(
                                                                ctx -> clear(
                                                                        ctx.getSource(),
                                                                        category(ctx)
                                                                )
                                                        )
                                        )
                                        .then(makeOptionCommand())
                        )
        );
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> makeOptionCommand() {
        return argument("option", StringArgumentType.word())
                .suggests(
                        (ctx, builder)
                                -> CommandSource.suggestMatching(
                                suggestOptionName(category(ctx)),
                                builder
                        )
                )
                .then(
                        argument("value", StringArgumentType.word())
                                .suggests((ctx, builder)
                                        -> CommandSource.suggestMatching(suggestOptionValue(category(ctx), option(ctx)), builder))
                                .executes(
                                        ctx -> setOption(
                                                ctx.getSource(),
                                                category(ctx),
                                                option(ctx),
                                                StringArgumentType.getString(ctx, "value")
                                        )
                                )
                );
    }

    private static String category(CommandContext<FabricClientCommandSource> ctx) {
        return StringArgumentType.getString(ctx, "category");
    }

    private static String option(CommandContext<FabricClientCommandSource> ctx) {
        return StringArgumentType.getString(ctx, "option");
    }

    private static int list(FabricClientCommandSource source) {
        GameLogs.getCategories().forEach(category -> source.sendFeedback(makeMessage(category)));
        return Command.SINGLE_SUCCESS;
    }

    private static Text makeMessage(String category) {
        GameLog log = GameLogs.getLog(category);
        assert log != null;
        MutableText text = Text.empty()
                .append(TextFactory.onOrOff(log.isActive()))
                .styled(style -> style
                        .withHoverEvent(
                                new HoverEvent.ShowText(LogCommandText.CLICK_TO_TOGGLE.text())
                        )
                        .withClickEvent(
                                new ClickEvent.SuggestCommand(String.format("/%slog %s", Commands.PREFIX, category))
                        )
                );
        return TextFactory.listEntry(category)
                .append(Texts.bracketed(text));
    }

    private static int clear(FabricClientCommandSource source, String category) {
        GameLog log = GameLogs.getLog(category);
        if (log == null) {
            source.sendFeedback(LogCommandText.CATEGORY_NOT_EXIST.text(category));
        } else {
            log.setActive(false);
            source.sendFeedback(LogCommandText.UNSUBSCRIBE.text(category));
        }
        return 0;
    }

    private static int toggle(FabricClientCommandSource source, String category) {
        GameLog log = GameLogs.getLog(category);
        if (log == null) {
            source.sendFeedback(LogCommandText.CATEGORY_NOT_EXIST.text(category));
        } else {
            log.toggle();
            LogCommandText message = log.isActive() ? LogCommandText.SUBSCRIBE : LogCommandText.UNSUBSCRIBE;
            source.sendFeedback(message.text(category));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clear(FabricClientCommandSource source) {
        int success = 0;
        for (GameLog log : GameLogs.getLogs()) {
            if (log.isActive()) {
                log.setActive(false);
                success += 1;
            }
        }
        source.sendFeedback(LogCommandText.UNSUBSCRIBE_ALL.text(success));
        return success;
    }

    private static Collection<String> suggestOptionName(String category) {
        GameLog log = GameLogs.getLog(category);
        return log == null ? Collections.emptySet() : log.getOptionNames();
    }

    private static Collection<String> suggestOptionValue(String category, String option) {
        GameLog log = GameLogs.getLog(category);
        if (log == null) return Collections.emptySet();
        if (log.hasOption(option)) return Collections.singleton(log.getOption(option));
        return Collections.emptySet();
    }

    private static int setOption(FabricClientCommandSource source, String category, String option, String value) {
        GameLog log = GameLogs.getLog(category);
        if (log == null) {
            source.sendFeedback(LogCommandText.CATEGORY_NOT_EXIST.text(category));
            return 0;
        }
        log.setOption(option, value);
        source.sendFeedback(LogCommandText.SET_OPTION.text(category, option, value));
        return Command.SINGLE_SUCCESS;
    }

}
