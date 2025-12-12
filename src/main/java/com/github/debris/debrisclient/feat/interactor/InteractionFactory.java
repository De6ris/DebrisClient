package com.github.debris.debrisclient.feat.interactor;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.localization.InteractionText;
import com.github.debris.debrisclient.unsafe.litematica.LitematicaAccessor;
import com.github.debris.debrisclient.util.BlockUtil;
import com.github.debris.debrisclient.util.Predicates;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InteractionFactory {
    public static boolean addBlockTask(Minecraft client, BlockPredicate predicate, boolean clearIfRunning) {
        return addBlockTask(client, predicate.stateTester, clearIfRunning);
    }

    public static boolean addBlockTask(Minecraft client, BiPredicate<Level, BlockPos> predicate, boolean clearIfRunning) {
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

    private static void addBlockTaskInternal(Minecraft client, BlockInteractor instance, BiPredicate<Level, BlockPos> predicate) {
        ClientLevel world = client.level;
        Collection<BlockPos> targets = new HashSet<>();
        LitematicaAccessor.streamBlockPos().forEach(pos -> {
            if (predicate.test(world, pos)) targets.add(pos.immutable());// otherwise the same object
        });
        if (targets.isEmpty()) {
            InfoUtils.sendVanillaMessage(InteractionText.NO_MATCHING_BLOCKS.translate());
        } else {
            InfoUtils.sendVanillaMessage(InteractionText.FOUND_BLOCKS.translate(targets.size()));
            instance.addAll(targets);
        }
    }

    public static boolean addEntityTask(Minecraft client, boolean clearIfRunning) {
        return addEntityTask(client, entity -> true, clearIfRunning);
    }

    public static boolean addEntityTask(Minecraft client, Predicate<Entity> predicate, boolean clearIfRunning) {
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
    private static void addEntityTaskInternal(Minecraft client, EntityInteractor instance, Predicate<Entity> predicate) {
        ClientLevel world = client.level;
        Set<Entity> targets = LitematicaAccessor.streamBlockBox()
                .map(AABB::of)
                .flatMap(x -> world.getEntities(client.player, x).stream())
                .collect(Collectors.toSet());
        if (targets.isEmpty()) {
            InfoUtils.sendVanillaMessage(InteractionText.NO_MATCHING_ENTITIES.translate());
        } else {
            InfoUtils.sendVanillaMessage(InteractionText.FOUND_ENTITIES.translate(targets.size()));
            instance.addAll(targets);
        }
    }

    public enum BlockPredicate implements StringRepresentable {
        CONTAINER(BlockUtil::isContainer),
        NON_CONTAINER((world, pos) -> {
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) return false;
            //noinspection RedundantIfStatement
            if (BlockUtil.isContainer(world, pos)) return false;
            return true;
        }),
        ;

        private final BiPredicate<Level, BlockPos> stateTester;

        BlockPredicate(BiPredicate<Level, BlockPos> stateTester) {
            this.stateTester = stateTester;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
