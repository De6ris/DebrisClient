package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InteractionFactory {
    public static boolean addBlockTask(MinecraftClient client, BlockPredicate predicate, boolean clearIfRunning) {
        return addBlockTask(client, predicate.stateTester, clearIfRunning);
    }

    public static boolean addBlockTask(MinecraftClient client, BiPredicate<World, BlockPos> predicate, boolean clearIfRunning) {
        if (!ModReference.hasMod(ModReference.Litematica)) return false;
        if (Predicates.notInGame(client)) return false;
        BlockInteractor instance = BlockInteractor.INSTANCE;
        if (clearIfRunning && instance.clearAndInform()) {
            return true;
        } else {
            addBlockTaskInternal(client, instance, predicate);
        }
        return true;
    }

    private static void addBlockTaskInternal(MinecraftClient client, BlockInteractor instance, BiPredicate<World, BlockPos> predicate) {
        ClientWorld world = client.world;
        Collection<BlockPos> targets = new HashSet<>();
        LitematicaAccessor.streamBlockPos().forEach(pos -> {
            if (predicate.test(world, pos)) targets.add(pos.toImmutable());// otherwise the same object
        });
        if (targets.isEmpty()) {
            InfoUtils.printActionbarMessage("交互选区内方块: 未找到匹配目标");
        } else {
            InfoUtils.printActionbarMessage(String.format("交互选区内方块: 已找到%d处方块", targets.size()));
            instance.addAll(targets);
        }
    }

    public static boolean addEntityTask(MinecraftClient client, boolean clearIfRunning) {
        return addEntityTask(client, entity -> true, clearIfRunning);
    }

    public static boolean addEntityTask(MinecraftClient client, Predicate<Entity> predicate, boolean clearIfRunning) {
        if (!ModReference.hasMod(ModReference.Litematica)) return false;
        if (Predicates.notInGame(client)) return false;
        EntityInteractor instance = EntityInteractor.INSTANCE;
        if (clearIfRunning && instance.clearAndInform()) {
            return true;
        } else {
            addEntityTaskInternal(client, instance, predicate);
        }
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    private static void addEntityTaskInternal(MinecraftClient client, EntityInteractor instance, Predicate<Entity> predicate) {
        ClientWorld world = client.world;
        Set<Entity> targets = LitematicaAccessor.streamBlockBox()
                .map(Box::from)
                .flatMap(x -> world.getOtherEntities(client.player, x).stream())
                .collect(Collectors.toSet());
        if (targets.isEmpty()) {
            InfoUtils.printActionbarMessage("交互选区内实体: 未找到实体");
        } else {
            InfoUtils.printActionbarMessage(String.format("交互选区内实体: 已找到%d处实体", targets.size()));
            instance.addAll(targets);
        }
    }

    public enum BlockPredicate implements StringIdentifiable {
        CONTAINER(BlockUtil::isContainer),
        NON_CONTAINER((world, pos) -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) return false;
            //noinspection RedundantIfStatement
            if (BlockUtil.isContainer(world, pos)) return false;
            return true;
        }),
        ;

        private final BiPredicate<World, BlockPos> stateTester;

        BlockPredicate(BiPredicate<World, BlockPos> stateTester) {
            this.stateTester = stateTester;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
