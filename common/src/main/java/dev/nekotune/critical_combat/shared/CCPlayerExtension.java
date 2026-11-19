package dev.nekotune.critical_combat.shared;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.mixin.minecraft.shared.PlayerMixin;
import dev.nekotune.critical_combat.util.TickDataTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

/**
 * Extended logic appended to Player entities.
 * 
 * @see PlayerMixin
 */
@ParametersAreNonnullByDefault
public final class CCPlayerExtension {

    /**
     * Returns the extension associated with the given player.
     * 
     * @param player The player to get the extension for
     * @return CCPlayerExtension object for the given player
     */
    public static CCPlayerExtension get(final Player player) {
        return ((CCPlayerExtension.MixinHolder) player).critical_combat$getPlayerExtension();
    }

    public final CameraTracker cameraTracker;

    public CCPlayerExtension(final Player player) {
        this.cameraTracker = new CameraTracker(player);
    }

    /**
     * @see PlayerMixin#critical_combat$tickPlayerExtension
     */
    public void tick() {
        this.cameraTracker.tick();
    }

    /**
     * Tracks the player's camera movement across
     * {@link CameraTracker#HISTORY_TICKS} ticks.
     */
    public static final class CameraTracker extends TickDataTracker<Vec2> {
        private static final int HISTORY_TICKS = 3;

        public CameraTracker(final Player player) {
            super(HISTORY_TICKS, () -> new Vec2(player.getYRot(), player.getXRot()));
        }

        @Override
        protected Vec2 compute(final Vec2 presentValue, final Collection<Vec2> oldValues) {
            float summedDeltaYaw = 0f;
            float summedDeltaPitch = 0f;
            for (final Vec2 oldValue : oldValues) {
                summedDeltaYaw += Mth.wrapDegrees(presentValue.x - oldValue.x);
                summedDeltaPitch += presentValue.y - oldValue.y;
            }
            final int ticksPassed = oldValues.size();
            return new Vec2(summedDeltaYaw / ticksPassed, summedDeltaPitch / ticksPassed);
        }
    }

    @FunctionalInterface
    public static interface MixinHolder {
        public CCPlayerExtension critical_combat$getPlayerExtension();
    }
}
