package com.github.Debris.DebrisClient.util;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.feat.BlockInteractor;
import com.github.Debris.DebrisClient.feat.WorldState;
import com.github.Debris.DebrisClient.inventory.util.InventoryUtil;
import com.github.Debris.DebrisClient.unsafe.itemScroller.UtilCaller;
import com.github.Debris.DebrisClient.unsafe.litematica.LitematicaAccessor;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class MiscUtil {
    public static void onTradeInfoUpdate(MinecraftClient client) {
        if (DCCommonConfig.OrientedAutoTrading.getBooleanValue()) runOrientedTrading(client);
    }

    public static void runOrientedTrading(MinecraftClient client) {
        if (!Predicates.hasMod(ModReference.ItemScroller)) return;
        Screen currentScreen = client.currentScreen;
        if (currentScreen instanceof MerchantScreen merchantScreen) {
            MerchantScreenHandler merchantContainer = merchantScreen.getScreenHandler();
            TradeOfferList recipes = merchantContainer.getRecipes();
            List<String> targets = DCCommonConfig.TradingTargets.getStrings();
            for (int i = 0; i < recipes.size(); i++) {
                ItemStack sellItem = recipes.get(i).getSellItem();
                if (isItemInList(sellItem, targets)) {
                    UtilCaller.tryHardTrading(i);
                }
            }
            merchantScreen.close();
        }
    }

    public static void runAutoThrow() {
        List<String> identifiers = DCCommonConfig.AutoThrowWhiteList.getStrings();
        for (Slot slot : InventoryUtil.getInventoryContainer().slots) {
            if (isItemInList(slot.getStack(), identifiers)) {
                InventoryUtil.dropStack(slot);
            }
        }
    }

    private static boolean isItemInList(ItemStack itemStack, List<String> identifiers) {
        for (String string : identifiers) {
            Identifier identifier = Identifier.of(string);
            Item item = Registries.ITEM.get(identifier);
            if (itemStack.isOf(item)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean alignWithEnderEye(MinecraftClient client) {
        if (Predicates.notInGame(client)) return false;
        ClientPlayerEntity player = client.player;
        Box expand = player.getBoundingBox().expand(32);
        List<Entity> list = Lists.newArrayList();
        client.world.collectEntitiesByType(TypeFilter.instanceOf(EyeOfEnderEntity.class), expand, entity -> true, list, 1);
        if (list.isEmpty()) return false;
        lookAtDirection(player, list.getFirst().getEyePos().subtract(player.getEyePos()));
        return true;
    }

    private static void lookAtDirection(ClientPlayerEntity player, Vec3d directionVec) {
        double dx = directionVec.x;
        double dy = directionVec.y;
        double dz = directionVec.z;
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), yaw, pitch);
    }

    public static void runAutoExtinguisher(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;
        Predicate<BlockState> fireTest = state -> state.isOf(Blocks.FIRE);
        InteractionUtil.digNear(client, fireTest);
    }

    public static void onThunderSync(boolean thunder) {
        if (!DCCommonConfig.MonitorThunderWeather.getBooleanValue()) return;
        if (WorldState.THUNDER != thunder) {
            ChatUtil.addLocalMessage(Text.literal(thunder ? "雷暴开始了" : "雷暴结束了"));
        }
        WorldState.THUNDER = thunder;
    }

    @SuppressWarnings("ConstantConditions")
    public static void runAutoBulletCatcher(MinecraftClient client) {
        if (Predicates.notInGame(client)) return;
        Box box = client.player.getBoundingBox().expand(3.0D);
        client.world.getEntitiesByClass(ShulkerBulletEntity.class, box, EntityPredicates.VALID_ENTITY).forEach(x -> InteractionUtil.attackEntity(client, x));
        client.world.getEntitiesByClass(FireballEntity.class, box, EntityPredicates.VALID_ENTITY).forEach(x -> InteractionUtil.attackEntity(client, x));
    }

    @SuppressWarnings("DataFlowIssue")
    public static boolean tryOpenSelectionContainers(MinecraftClient client) {
        if (!Predicates.hasMod(ModReference.Litematica)) return false;
        if (Predicates.notInGame(client)) return false;
        if (BlockInteractor.running()) {
            BlockInteractor.stop();
            InfoUtils.printActionbarMessage("打开选区内容器: 已停止");
            return true;
        }
        ClientWorld world = client.world;
        Collection<BlockPos> targets = new HashSet<>();
        LitematicaAccessor.selectionRun(pos -> {
            BlockEntity blockEntity = world.getChunk(pos).getBlockEntity(pos);
            if (blockEntity == null) return;
            if (blockEntity instanceof ScreenHandlerFactory) targets.add(pos.toImmutable());// otherwise the same object
        });
        if (targets.isEmpty()) {
            InfoUtils.printActionbarMessage("打开选区内容器: 未找到容器");
            return true;
        }
        BlockInteractor.addAll(targets);
        return true;
    }
}
