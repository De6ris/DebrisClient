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
                .executes(ctx -> execute(ctx.getSource()))
                .then(argument("filter", entities())
                        .executes(ctx -> execute(ctx.getSource(), ctx.getArgument("filter", CEntitySelector.class))))
        );
    }

    private static int execute(FabricClientCommandSource source) {
        return execute(source, Streams.stream(source.getWorld().getEntities()));
    }

    private static int execute(FabricClientCommandSource source, CEntitySelector entitySelector) throws CommandSyntaxException {
        return execute(source, entitySelector.findEntities(source).stream());
    }

    private static int execute(FabricClientCommandSource source, Stream<? extends Entity> stream) {
        Map<? extends EntityType<?>, Distribution> map = stream.collect(Collectors.groupingBy(Entity::getType))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> distributeByPosition(entry.getValue())
                ));
        source.sendFeedback(Text.literal(String.format("已找到%d种实体, 共%d个", map.size(), map.values().stream().mapToInt(Distribution::getTotal).sum())));
        map.forEach((entityType, distribution) -> source.sendFeedback(getFeedback(source, entityType, distribution)));
        return Command.SINGLE_SUCCESS;
    }

    private static MutableText getFeedback(FabricClientCommandSource source, EntityType<?> entityType, Distribution distribution) {
        MutableText feedback = TextFactory.listEntry(
                Text.empty()
                        .append(entityType.getName())
                        .styled(style -> style.withColor(ColorUtil.getColor(entityType)))
                        .append(String.format("(%d)", distribution.getTotal()))
        );
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
            DistributionEntry entry = distribution.get(i);
            BlockPos blockPos = entry.pos;
            int count = entry.count;

            feedback.append(Text.literal(String.valueOf(count)).styled(
                    style -> style.withColor(Formatting.AQUA)
                            .withHoverEvent(new HoverEvent.ShowText(Texts.bracketed(Text.translatable
                                    ("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()))))
                            .withClickEvent(new ClickEvent.SuggestCommand(TeleportUtil.suggestCommand(source.getClient(), blockPos)))
            ));

            if (i == printSize - 1) {
                if (reduced) {
                    feedback.append(Text.literal("...").styled(
                            style -> style.withColor(Formatting.LIGHT_PURPLE)
                                    .withHoverEvent(new HoverEvent.ShowText(Text.literal("省略了" + (originalSize - printSize) + "处结果")))
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

    private static Distribution distributeByPosition(List<? extends Entity> entities) {
        Map<BlockPos, Long> distribution = entities.stream().collect(Collectors.groupingBy(Entity::getBlockPos, Collectors.counting()));
        Comparator<DistributionEntry> comparator = Comparator.comparingInt(DistributionEntry::count);
        List<DistributionEntry> list = distribution.entrySet().stream()
                .map(x -> new DistributionEntry(x.getKey(), x.getValue())).sorted(comparator.reversed()).toList();
        return new Distribution(list);
    }

    private record Distribution(List<DistributionEntry> entries) {
        private int getTotal() {
            return entries.stream().mapToInt(DistributionEntry::count).sum();
        }

        private int size() {
            return entries.size();
        }

        private DistributionEntry get(int index) {
            return entries.get(index);
        }
    }

    private record DistributionEntry(BlockPos pos, int count) {
        private DistributionEntry(BlockPos pos, long count) {
            this(pos, (int) count);
        }
    }
}
