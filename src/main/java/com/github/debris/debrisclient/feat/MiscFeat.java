package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.function.Predicate;

public class MiscFeat {
    @SuppressWarnings("ConstantConditions")
    public static boolean alignWithEnderEye(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;
        ClientPlayerEntity player = client.player;
        Box expand = player.getBoundingBox().expand(32);
        List<Entity> list = Lists.newArrayList();
        client.world.collectEntitiesByType(TypeFilter.instanceOf(EyeOfEnderEntity.class), expand, entity -> true, list, 1);
        if (list.isEmpty()) return false;
        PlayerRotation.lookAtEntity(player, list.getFirst());
        return true;
    }

    public static void runAutoExtinguisher(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;
        Predicate<BlockState> fireTest = state -> state.isOf(Blocks.FIRE);
        BlockDigger.digNear(client, fireTest);
    }

    @SuppressWarnings("ConstantConditions")
    public static void runAutoBulletCatcher(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;
        Box box = client.player.getBoundingBox().expand(3.0D);
        client.world.getEntitiesByClass(ShulkerBulletEntity.class, box, EntityPredicates.VALID_ENTITY).forEach(x -> InteractionUtil.attackEntity(client, x));
        client.world.getEntitiesByClass(FireballEntity.class, box, EntityPredicates.VALID_ENTITY).forEach(x -> InteractionUtil.attackEntity(client, x));
    }

}
