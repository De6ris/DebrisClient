package com.github.debris.debrisclient.feat.commandmacro.generator;

import java.util.OptionalInt;

public interface Context {
    Type getType();

    record Default(
            int code1,
            int code2
    ) implements Context {

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
    ) implements Context {
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
