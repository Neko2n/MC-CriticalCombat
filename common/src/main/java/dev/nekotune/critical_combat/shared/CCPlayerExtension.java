package dev.nekotune.critical_combat.shared;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.mixin.minecraft.shared.PlayerMixin;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import dev.nekotune.critical_combat.util.TickDataTracker;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    public final Player player;
    public final CameraTracker cameraTracker;
    private float attackHeat = 0f;

    public CCPlayerExtension(final Player player) {
        this.player = player;
        this.cameraTracker = new CameraTracker(player);
    }

    /**
     * @see PlayerMixin#critical_combat$tickPlayerExtension
     */
    public void tick(final CallbackInfo ci) {
        this.cameraTracker.tick();

        // Drain attack heat passively over time
        final float heatLoss = CCConfig.HEAT_DRAIN.get().floatValue() / SharedConstants.TICKS_PER_SECOND;
        this.attackHeat = Math.clamp(this.attackHeat - heatLoss, 0f, 1f);

        // TODO remove debug
        final @Nullable WeaponType heldWeaponType = WeaponTypes.get(this.player.getWeaponItem().getItem());
        if (heldWeaponType == WeaponType.LIGHT && !this.player.level().isClientSide() && player.isCrouching()) {
            CriticalCombat.LOG.info("HEAT: {}", this.attackHeat);
        }
    }

    /**
     * @see PlayerMixin#critical_combat$hookAttack
     */
    public void attack(final Entity target, final CallbackInfo ci) {

        // Accrue heat when attacking mobs with LIGHT-weapons
        final boolean canAttack = target.isAttackable()
                && !target.skipAttackInteraction(this.player);
        if (canAttack) {
            if (target instanceof LivingEntity) {
                final @Nullable WeaponType heldWeaponType = WeaponTypes.get(this.player.getWeaponItem().getItem());
                if (heldWeaponType == WeaponType.LIGHT) {
                    float heatGain = CCConfig.HEAT_PER_HIT.get().floatValue();
                    if (heldWeaponType.shouldCrit(this.player, target))
                        heatGain *= 2f;
                    this.attackHeat = Math.clamp(this.attackHeat + heatGain, 0f, 1f);
                }
            }
        }
    }

    /**
     * @see PlayerMixin#critical_combat$hookHurt
     */
    public void hurt(final DamageSource source, final float amount,
            final CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (CCConfig.HEAT_RESETS_ON_HURT.get())
                this.attackHeat = 0f;
        }
    }

    /**
     * @return The player's current "heat" (% damage with LIGHT-type weapons)
     */
    public float getHeat() {
        return this.attackHeat;
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
