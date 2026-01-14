package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.feat.CommandMacro;
import com.github.debris.debrisclient.feat.CommandQueue;
import com.github.debris.debrisclient.localization.CommandMacroCommandText;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.util.TextFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import fi.dy.masa.malilib.util.JsonUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCCommandMacroCommand {
    private static final String FOLDER_NAME = "command_macros";
    private static final Path MACRO_DIR = DebrisClient.CONFIG_DIR.resolve(FOLDER_NAME);

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
                CommandMacroCommandText.HELP.translate(
                        TextFactory.here()
                                .withStyle(
                                        style -> style.withClickEvent(new ClickEvent.OpenUrl(MACRO_DIR.toUri()))
                                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(MACRO_DIR.toAbsolutePath().toString())))
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
        JsonObject jsonObject = new CommandMacro(5, List.of("hello world", "/say 1")).save();
        Path path = MACRO_DIR.resolve("example.json");
        JsonUtils.writeJsonToFileAsPath(jsonObject, path);
        source.sendFeedback(
                CommandMacroCommandText.EXAMPLE_CREATED.translate(
                        TextFactory.here().withStyle(
                                style -> style.withClickEvent(new ClickEvent.OpenUrl(path.toUri()))
                                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(path.toAbsolutePath().toString())))
                        )
                )
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int stop(FabricClientCommandSource source) {
        CommandQueue.stop();
        source.sendFeedback(CommandMacroCommandText.STOPPED.translate());
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Should consume this stream, otherwise the thread would lock.
     */
    private static List<String> listFiles() {
        try (Stream<Path> stream = Files.list(MACRO_DIR)) {
            return stream.filter(Files::isRegularFile).map(x -> x.getFileName().toString()).toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    private static int run(FabricClientCommandSource source, String file) {
        Path filePath = MACRO_DIR.resolve(file);

        if (!Files.exists(filePath)) {
            source.sendFeedback(CommandMacroCommandText.FILE_NOT_FOUND.translate());
            return 0;
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonObject()) {
                source.sendFeedback(CommandMacroCommandText.NOT_JSON.translate());
                return 0;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            CommandMacro macro = CommandMacro.load(jsonObject);
            if (macro.period() < 0) {
                source.sendFeedback(CommandMacroCommandText.ILLEGAL_PERIOD.translate());
                return 0;
            }
            CommandQueue.run(macro);
        } catch (Exception e) {
            source.sendFeedback(CommandMacroCommandText.READ_FILE_ERROR.translate());
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

}
