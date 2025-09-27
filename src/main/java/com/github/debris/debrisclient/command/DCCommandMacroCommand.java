package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.DebrisClient;
import com.github.debris.debrisclient.feat.CommandMacro;
import com.github.debris.debrisclient.feat.CommandQueue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

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

    private static int stop(FabricClientCommandSource source) {
        CommandQueue.stop();
        source.sendFeedback(Text.literal("已停止"));
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
            source.sendFeedback(Text.literal("文件不存在"));
            return 0;
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                CommandMacro macro = CommandMacro.load(jsonObject);
                CommandQueue.run(macro);
            } else {
                source.sendFeedback(Text.literal("文件不符合Json对象格式"));
            }
        } catch (Exception e) {
            source.sendFeedback(Text.literal("读取文件失败"));
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }

}
