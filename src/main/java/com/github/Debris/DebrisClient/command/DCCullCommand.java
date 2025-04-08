package com.github.Debris.DebrisClient.command;

import com.github.Debris.DebrisClient.config.DCCommonConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DCCullCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(Commands.PREFIX + "cull")
                .then(literal("help")
                        .executes(ctx -> help(ctx.getSource())))
                .then(literal("block_entity")
                        .then(makeCommand("block_entity_type", RegistryKeys.BLOCK_ENTITY_TYPE, key -> DCCommonConfig.CullBlockEntityList.getStrings().add(key))))
                .then(literal("entity")
                        .then(makeCommand("entity_type", RegistryKeys.ENTITY_TYPE, key1 -> DCCommonConfig.CullEntityList.getStrings().add(key1))))
                .then(literal("particle")
                        .then(makeCommand("particle_type", RegistryKeys.PARTICLE_TYPE, key2 -> DCCommonConfig.CullParticleList.getStrings().add(key2))))
                .then(literal("sound")
                        .then(makeCommand("sound_type", RegistryKeys.SOUND_EVENT, key3 -> DCCommonConfig.MuteSoundList.getStrings().add(key3))))

        );
    }

    private static <T> RequiredArgumentBuilder<FabricClientCommandSource, ?> makeCommand(String argument, RegistryKey<Registry<T>> registryKey, Consumer<String> keyReader) {
        return CommandFactory.ofRegistryKey(argument, registryKey, (ctx, reference) -> {
            String key = reference.registryKey().getValue().toString();
            keyReader.accept(key);
            ctx.getSource().sendFeedback(Text.literal("已添加到列表: " + key));
            return Command.SINGLE_SUCCESS;
        });
    }

    private static int help(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal("此命令可将指定类型的方块实体, 实体, 粒子, 音效屏蔽."));
        source.sendFeedback(Text.literal("被添加的类型将储存到本模组配置文件中."));
        source.sendFeedback(Text.literal("可在配置GUI中查看, 和增删操作."));
        source.sendFeedback(Text.literal("此命令方便之处在于能tab补全."));
        return Command.SINGLE_SUCCESS;
    }

}
