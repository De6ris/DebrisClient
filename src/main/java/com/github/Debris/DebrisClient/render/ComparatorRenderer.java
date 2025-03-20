package com.github.Debris.DebrisClient.render;

import com.github.Debris.DebrisClient.compat.ModReference;
import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.github.Debris.DebrisClient.unsafe.magicLib.MagicLibTextRenderer;
import com.github.Debris.DebrisClient.unsafe.miniHud.MiniHudConfigAccessor;
import com.github.Debris.DebrisClient.util.RayTraceUtil;
import fi.dy.masa.malilib.util.WorldUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ComparatorRenderer {
    @SuppressWarnings("ConstantConditions")
    public static void render(MinecraftClient client) {
        Optional<HitResult> optionalTrace = RayTraceUtil.getPlayerRayTrace(client);
        if (optionalTrace.isEmpty()) return;
        HitResult hitResult = optionalTrace.get();

        if (hitResult.getType() != HitResult.Type.BLOCK) return;
        BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();

        World world = WorldUtils.getBestWorld(client);
        // get it through chunk, since the server return you null if you call world.getBlockEntity directly on render thread
        Optional<ComparatorBlockEntity> optionalComparator = world.getWorldChunk(blockPos).getBlockEntity(blockPos, BlockEntityType.COMPARATOR);
        if (optionalComparator.isEmpty()) return;
        ComparatorBlockEntity comparator = optionalComparator.get();

        MagicLibTextRenderer.renderText(comparator.getOutputSignal(), blockPos);
    }

    public static void onRenderWorldLast(MinecraftClient client) {
        if (shouldSkip()) return;
        ComparatorRenderer.render(client);
    }

    private static boolean shouldSkip() {
        if (!DCCommonConfig.InventoryPreviewSupportComparator.getBooleanValue()) return true;
        if (!FabricLoader.getInstance().isModLoaded(ModReference.MiniHud)) return true;
        if (!MiniHudConfigAccessor.isPreviewingInventory()) return true;
        if (!FabricLoader.getInstance().isModLoaded(ModReference.MagicLibMCApi)) return true;
        return false;
    }
}
