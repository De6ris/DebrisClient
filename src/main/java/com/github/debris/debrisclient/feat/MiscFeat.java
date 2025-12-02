package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.util.InteractionUtil;
import com.github.debris.debrisclient.util.Predicates;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class MiscFeat {
    @SuppressWarnings("ConstantConditions")
    public static boolean alignWithEnderEye(Minecraft client) {
        if (Predicates.notInGame(client)) return false;
        LocalPlayer player = client.player;
        AABB expand = player.getBoundingBox().inflate(32);
        List<Entity> list = Lists.newArrayList();
        client.level.getEntities(EntityTypeTest.forClass(EyeOfEnder.class), expand, entity -> true, list, 1);
        if (list.isEmpty()) return false;
        PlayerRotation.lookAtEntity(player, list.getFirst());
        return true;
    }

    public static void runAutoExtinguisher(Minecraft client) {
        if (Predicates.notInGame(client)) return;
        Predicate<BlockState> fireTest = state -> state.is(Blocks.FIRE);
        BlockDigger.digNear(client, fireTest);
    }

    @SuppressWarnings("ConstantConditions")
    public static void runAutoBulletCatcher(Minecraft client) {
        if (Predicates.notInGame(client)) return;
        AABB box = client.player.getBoundingBox().inflate(3.0D);
        client.level.getEntitiesOfClass(ShulkerBullet.class, box, EntitySelector.ENTITY_STILL_ALIVE).forEach(x -> InteractionUtil.attackEntity(client, x));
        client.level.getEntitiesOfClass(LargeFireball.class, box, EntitySelector.ENTITY_STILL_ALIVE).forEach(x -> InteractionUtil.attackEntity(client, x));
    }

}
