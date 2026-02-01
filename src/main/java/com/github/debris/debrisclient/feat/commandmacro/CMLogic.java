package com.github.debris.debrisclient.feat.commandmacro;

import com.github.debris.debrisclient.compat.ModReference;
import com.github.debris.debrisclient.localization.GeneralText;
import com.github.debris.debrisclient.unsafe.LitematicaAccess;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class CMLogic {
    public static final int DEFAULT_PERIOD = 5;
    public static final String DEFAULT_COMMAND = "/player bot_${code} spawn at ${pos}";
    public static final String DEFAULT_FILE = "test.json";

    public static final String CODE = "${code}";
    public static final String POS = "${pos}";

    @SuppressWarnings("OptionalIsPresent")
    public static Either<BoundingBox, Component> getBox() {
        if (!ModReference.hasMod(ModReference.Litematica)) {
            return Either.right(GeneralText.FEATURE_REQUIRES_MOD.translate(ModReference.Litematica));
        }
        Optional<BoundingBox> optional = LitematicaAccess.streamBlockBox().findFirst();
        if (optional.isPresent()) return Either.left(optional.get());
        return Either.right(Component.literal("未找到选区").withStyle(ChatFormatting.RED));
    }

    public static CMContext.Type getType(String command) {
        return command.contains(CMLogic.POS) ? CMContext.Type.SPAWN : CMContext.Type.DEFAULT;
    }

    public static boolean save(CMInputData record) {
        return generateMacro(record).saveToFile(record.file());
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "DataFlowIssue", "SwitchStatementWithTooFewBranches"})
    public static CommandMacro generateMacro(CMInputData record) {
        CMContext context = record.context();
        CMContext.Type type = context.getType();

        String command = record.command();
        List<String> commands;
        switch (type) {
            case SPAWN -> {
                CMContext.Spawn asSpawn = (CMContext.Spawn) context;
                YPosMode yPosMode = asSpawn.yPosMode();
                BiFunction<Integer, Integer, Integer> yAccess;
                if (yPosMode == YPosMode.FIXED_VALUE) {
                    int fixedY = asSpawn.yPos().getAsInt();
                    yAccess = (x, z) -> fixedY;
                } else {
                    ClientLevel level = Minecraft.getInstance().level;
                    yAccess = (x, z) -> level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                }
                int startX = asSpawn.startX();
                int startZ = asSpawn.startZ();
                int endX = asSpawn.endX();
                int endZ = asSpawn.endZ();
                int incrementX = endX > startX ? 1 : -1;
                int incrementZ = endZ > startZ ? 1 : -1;
                commands = new ArrayList<>((Math.abs(endX - startX) + 1) * (Math.abs(endZ - startZ) + 1));
                int code = 0;
                for (int x = startX; x != endX + incrementX; x += incrementX) {
                    for (int z = startZ; z != endZ + incrementZ; z += incrementZ) {
                        String pos = String.format("%d %d %d", x, yAccess.apply(x, z), z);
                        commands.add(command.replace(CODE, String.valueOf(code)).replace(POS, pos));
                        code++;
                    }
                }
            }
            default -> {
                CMContext.Default asDefault = (CMContext.Default) context;
                int code1 = asDefault.code1();
                int code2 = asDefault.code2();
                int increment = code2 > code1 ? 1 : -1;
                commands = new ArrayList<>(Math.abs(code2 - code1) + 1);
                for (int i = code1; i != code2 + increment; i += increment) {
                    commands.add(command.replace(CODE, String.valueOf(i)));
                }
            }
        }

        return new CommandMacro(record.period(), commands);
    }
}
