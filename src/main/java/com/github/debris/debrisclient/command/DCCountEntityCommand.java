package com.github.debris.debrisclient.command;

import com.github.debris.debrisclient.util.ColorUtil;
import com.github.debris.debrisclient.util.TeleportUtil;
import com.github.debris.debrisclient.util.TextFactory;
import com.google.common.collect.Streams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CEntitySelector;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.xpple.clientarguments.arguments.CEntityArgument.entities;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class DCCountEntityCommand {
    private static final int DISTRIBUTION_PRINT_LIMIT = 10;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal(Commands.PREFIX + "count_entity")
                        .executes(ctx -> execute(ctx.getSource()))
                        .then(
                                argument("filter", entities())
                                        .executes(
                                                ctx -> execute(
                                                        ctx.getSource(),
                                                        ctx.getArgument("filter", CEntitySelector.class)
                                                )
                                        )
                        )
        );
    }

    private static int execute(FabricClientCommandSource source) {
        return execute(source, Streams.stream(source.getLevel().entitiesForRendering()));
    }

    private static int execute(FabricClientCommandSource source, CEntitySelector entitySelector) throws CommandSyntaxException {
        return execute(source, entitySelector.findEntities(source).stream());
    }

    private static int execute(FabricClientCommandSource source, Stream<? extends Entity> stream) {
        List<Distribution> list = stream.collect(Collectors.groupingBy(Entity::getType))
                .entrySet().stream()
                .map(x -> distributeByPosition(x.getKey(), x.getValue()))
                .sorted(Comparator.comparing(Distribution::totalCount).reversed())
                .toList();
        source.sendFeedback(
                Component.literal(
                        String.format(
                                "已找到%d种实体, 共%d个",
                                list.size(),
                                list.stream().mapToInt(Distribution::totalCount).sum())
                )
        );
        list.forEach(distribution -> source.sendFeedback(getFeedback(source, distribution)));
        return Command.SINGLE_SUCCESS;
    }

    private static MutableComponent getFeedback(FabricClientCommandSource source, Distribution distribution) {
        EntityType<?> entityType = distribution.entityType;
        MutableComponent feedback = TextFactory.listEntry(
                Component.empty()
                        .append(entityType.getDescription())
                        .withStyle(style -> style.withColor(ColorUtil.getColor(entityType)))
                        .append(String.format("(%d)", distribution.totalCount))
        );
        int originalSize = distribution.entries.size();
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
            DistributionEntry entry = distribution.entries.get(i);
            BlockPos blockPos = entry.pos;
            int count = entry.count;

            feedback.append(Component.literal(String.valueOf(count)).withStyle(
                    style -> style.withColor(ChatFormatting.AQUA)
                            .withHoverEvent(new HoverEvent.ShowText(ComponentUtils.wrapInSquareBrackets(Component.translatable
                                    ("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()))))
                            .withClickEvent(new ClickEvent.SuggestCommand(TeleportUtil.suggestCommand(source.getClient(), blockPos)))
            ));

            if (i == printSize - 1) {
                if (reduced) {
                    feedback.append(Component.literal("...").withStyle(
                            style -> style.withColor(ChatFormatting.LIGHT_PURPLE)
                                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("省略了" + (originalSize - printSize) + "处结果")))
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

    private static Distribution distributeByPosition(EntityType<?> entityType, List<? extends Entity> entities) {
        Map<BlockPos, Long> distribution = entities
                .stream()
                .collect(Collectors.groupingBy(Entity::blockPosition, Collectors.counting()));
        Comparator<DistributionEntry> comparator = Comparator.comparingInt(DistributionEntry::count);
        List<DistributionEntry> list = distribution.entrySet().stream()
                .map(x -> new DistributionEntry(x.getKey(), x.getValue())).sorted(comparator.reversed())
                .toList();
        return new Distribution(entityType, list, entities.size());
    }

    private record Distribution(EntityType<?> entityType, List<DistributionEntry> entries, int totalCount) {
    }

    private record DistributionEntry(BlockPos pos, int count) {
        private DistributionEntry(BlockPos pos, long count) {
            this(pos, (int) count);
        }
    }
}
