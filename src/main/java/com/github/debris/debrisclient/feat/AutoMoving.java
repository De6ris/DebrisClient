package com.github.debris.debrisclient.feat;

import com.github.debris.debrisclient.config.DCCommonConfig;
import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

public class AutoMoving {
    public static boolean isAutoMoving() {
        return DCCommonConfig.AUTO_WALK.getBooleanValue() ||
                DCCommonConfig.AUTO_LEFT.getBooleanValue() ||
                DCCommonConfig.AUTO_BACK.getBooleanValue() ||
                DCCommonConfig.AUTO_RIGHT.getBooleanValue();
    }

    public static void handleMovement(Input input) {
        PlayerInput oldInput = input.playerInput;
        input.playerInput = new PlayerInput(
                oldInput.forward() || DCCommonConfig.AUTO_WALK.getBooleanValue(),
                oldInput.backward() || DCCommonConfig.AUTO_BACK.getBooleanValue(),
                oldInput.left() || DCCommonConfig.AUTO_LEFT.getBooleanValue(),
                oldInput.right() || DCCommonConfig.AUTO_RIGHT.getBooleanValue(),
                oldInput.jump(),
                oldInput.sneak(),
                oldInput.sprint()
        );
        float f = getMovementMultiplier(input.playerInput.forward(), input.playerInput.backward());
        float g = getMovementMultiplier(input.playerInput.left(), input.playerInput.right());
        input.movementVector = new Vec2f(g, f).normalize();
    }

    public static void clearMovement(Input input) {
        input.playerInput = PlayerInput.DEFAULT;
        input.movementVector = Vec2f.ZERO;
    }

    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }
}
