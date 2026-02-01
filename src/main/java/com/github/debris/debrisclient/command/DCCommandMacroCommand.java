package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.feat.CommandQueue;
import com.github.debris.debrisclient.feat.commandmacro.CMGenerator;
import com.github.debris.debrisclient.feat.commandmacro.CommandMacro;
import com.github.debris.debrisclient.localization.CommandMacroText;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.util.TextFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCCommandMacroCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "command_macro")
                        .then(
                                literal("help")
                                        .executes(ctx -> help(ctx.getSource()))
                        )
                        .then(
                                literal("example")
                                        .executes(ctx -> example(ctx.getSource()))
                        )
                        .then(
                                literal("gui")
                                        .executes(ctx -> gui(ctx.getSource()))
                        )
                        .then(
                                literal("stop")
                                        .executes(ctx -> stop(ctx.getSource()))
                        )
                        .then(
                                literal("run")
                                        .then(
                                                argument("file", StringArgumentType.string())
                                                        .suggests(CommandFactory.suggestMatching(() -> listFiles().stream()))
                                                        .executes(ctx ->
                                                                run(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "file")
                                                                )
                                                        )
                                        )
                        )
        );
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(
                CommandMacroText.HELP.translate(
                        TextFactory.here()
                                .withStyle(
                                        style -> style.withClickEvent(new ClickEvent.OpenUrl(CommandMacro.MACRO_DIR.toUri()))
                                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(CommandMacro.MACRO_DIR.toAbsolutePath().toString())))
                                ),
                        Component.literal("example")
                                .withStyle(
                                        style -> style.applyFormat(ChatFormatting.AQUA)
                                                .withClickEvent(new ClickEvent.SuggestCommand("/dccommand_macro example"))
                                                .withHoverEvent(new HoverEvent.ShowText(GeneralText.CLICK_TO_EXECUTE.translate()))
                                ),
                        Component.literal("run")
                                .withStyle(
                                        style -> style.applyFormat(ChatFormatting.AQUA)
                                                .withClickEvent(new ClickEvent.SuggestCommand("/dccommand_macro run example.json"))
                                                .withHoverEvent(new HoverEvent.ShowText(GeneralText.CLICK_TO_EXECUTE.translate()))
                                )

                )
        );
        return Command.SINGLE_SUCCESS;
    }


    private static int example(FabricClientCommandSource source) {
        CommandMacro example = new CommandMacro(5, List.of("hello world", "/say 1"));
        example.saveToFile("example.json");
        Path path = CommandMacro.MACRO_DIR.resolve("example.json");
        source.sendFeedback(
                CommandMacroText.EXAMPLE_CREATED.translate(
                        TextFactory.here().withStyle(
                                style -> style.withClickEvent(new ClickEvent.OpenUrl(path.toUri()))
                                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(path.toAbsolutePath().toString())))
                        )
                )
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int gui(FabricClientCommandSource source) {
        Component component = CMGenerator.openGui(source.getClient());
        if (component != null) source.sendFeedback(component);
        return Command.SINGLE_SUCCESS;
    }

    private static int stop(FabricClientCommandSource source) {
        CommandQueue.stop();
        source.sendFeedback(CommandMacroText.STOPPED.translate());
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Should consume this stream, otherwise the thread would lock.
     */
    private static List<String> listFiles() {
        try (Stream<Path> stream = Files.list(CommandMacro.MACRO_DIR)) {
            return stream.filter(Files::isRegularFile).map(x -> x.getFileName().toString()).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static int run(FabricClientCommandSource source, String file) {
        Component component = CommandMacro.runFile(file);
        if (component != null) {
            source.sendFeedback(component);
        }
        return Command.SINGLE_SUCCESS;
    }

}
