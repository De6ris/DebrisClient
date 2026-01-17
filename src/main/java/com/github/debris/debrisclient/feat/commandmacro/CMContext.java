package com.github.debris.debrisclient.feat.commandmacro;

import java.util.OptionalInt;

public interface CMContext {
    Type getType();

    record Default(
            int code1,
            int code2
    ) implements CMContext {

        @Override
        public Type getType() {
            return Type.DEFAULT;
        }
    }

    record Spawn(
            int startX,
            int startZ,
            int endX,
            int endZ,
            YPosMode yPosMode,
            OptionalInt yPos
    ) implements CMContext {
        @Override
        public Type getType() {
            return Type.SPAWN;
        }
    }

    enum Type {
        DEFAULT,
        SPAWN,
        ;
    }
}
