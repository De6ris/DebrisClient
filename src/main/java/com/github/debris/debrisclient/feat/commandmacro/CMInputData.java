package com.github.debris.debrisclient.feat.commandmacro;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.Component;

import java.util.OptionalInt;

public record CMInputData(
        CMContext context,
        int period,
        String command,
        String file
) {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Either<CMInputData, Component> parse(OptionalInt period,
                                                       String command,
                                                       OptionalInt code1,
                                                       OptionalInt code2,
                                                       OptionalInt startX,
                                                       OptionalInt startZ,
                                                       OptionalInt endX,
                                                       OptionalInt endZ,
                                                       YPosMode yPosMode,
                                                       OptionalInt yPos,
                                                       String file
    ) {
        if (period.isEmpty() || period.getAsInt() < 0) return Either.right(Component.literal("无效的时间间隔"));
        if (command.isEmpty()) return Either.right(Component.literal("指令为空"));

        CMContext.Type type;
        if (command.contains(CMLogic.POS)) {
            type = CMContext.Type.SPAWN;
            if (startX.isEmpty() || startZ.isEmpty() || endX.isEmpty() || endZ.isEmpty())
                return Either.right(Component.literal("无效的XZ坐标范围"));
            if (yPosMode == YPosMode.FIXED_VALUE && yPos.isEmpty())
                return Either.right(Component.literal("未提供Y坐标"));
        } else {
            type = CMContext.Type.DEFAULT;
            if (code1.isEmpty() || code2.isEmpty()) return Either.right(Component.literal("无效的编号范围"));
        }

        CMContext context;
        if (type == CMContext.Type.SPAWN) {
            context = new CMContext.Spawn(
                    startX.getAsInt(),
                    startZ.getAsInt(),
                    endX.getAsInt(),
                    endZ.getAsInt(),
                    yPosMode,
                    yPos
            );
        } else {
            context = new CMContext.Default(code1.getAsInt(), code2.getAsInt());
        }
        return Either.left(new CMInputData(context, period.getAsInt(), command, file));
    }
}
