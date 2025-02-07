package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.util.ColorUtil;
import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CEntitySelector;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.xpple.clientarguments.arguments.CEntityArgument.entities;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCCountEntityCommand {
    private static final int DISTRIBUTION_PRINT_LIMIT = 10;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "count_entity")
                .executes(ctx -> countEntity(ctx.getSource()))
                .then(argument("filter", entities())
                        .executes(ctx -> countEntity(ctx.getSource(), ctx.getArgument("filter", CEntitySelector.class))))
        );
    }

    private static int countEntity(FabricClientCommandSource source) {
        return countEntity(source, Streams.stream(source.getWorld().getEntities()));
    }

    private static int countEntity(FabricClientCommandSource source, CEntitySelector entitySelector) throws CommandSyntaxException {
        return countEntity(source, entitySelector.findEntities(source).stream());
    }

    private static int countEntity(FabricClientCommandSource source, Stream<? extends Entity> stream) {
        stream.collect(Collectors.groupingBy(Entity::getType))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> distributeByPosition(entry.getValue())
                ))
                .forEach((entityType, distribution) -> source.sendFeedback(getFeedback(entityType, distribution)));
        return Command.SINGLE_SUCCESS;
    }

    private static MutableText getFeedback(EntityType<?> entityType, List<Map.Entry<BlockPos, Long>> distribution) {
        MutableText feedback = Text.empty()
                .append("- ")
                .append(Text.empty().append(entityType.getName()).styled(style -> style.withColor(ColorUtil.getColor(entityType))))
                .append(String.format("(%d): ", distribution.stream().mapToLong(Map.Entry::getValue).sum()));
        int originalSize = distribution.size();
        int printSize;
        boolean reduced;
        if (originalSize > DISTRIBUTION_PRINT_LIMIT) {
            printSize = DISTRIBUTION_PRINT_LIMIT;
            reduced = true;
        } else {
            printSize = originalSize;
            reduced = false;
        }

        for (int i = 0; i < printSize; i++) {
            Map.Entry<BlockPos, Long> entry = distribution.get(i);
            BlockPos blockPos = entry.getKey();
            Long count = entry.getValue();
            feedback.append(Text.literal(String.valueOf(count)).styled(
                    style -> style.withColor(Formatting.AQUA)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Texts.bracketed(Text.translatable
                                            ("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()))))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "/tp " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ()))
            ));

            if (i == printSize - 1) {
                if (reduced) {
                    feedback.append(Text.literal("...").styled(
                            style -> style.withColor(Formatting.LIGHT_PURPLE)
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.literal("省略了" + (originalSize - printSize) + "处结果")))
                    ));
                } else {
                    feedback.append(".");
                }
            } else {
                feedback.append("+");
            }
        }

        return feedback;
    }

    private static List<Map.Entry<BlockPos, Long>> distributeByPosition(List<? extends Entity> entities) {
        Map<BlockPos, Long> distribution = entities.stream().collect(Collectors.groupingBy(Entity::getBlockPos, Collectors.counting()));
        Comparator<Map.Entry<BlockPos, Long>> comparator = Comparator.comparingLong(Map.Entry::getValue);
        return distribution.entrySet().stream().sorted(comparator.reversed()).toList();
    }
}
