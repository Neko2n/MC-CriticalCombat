package dev.nekotune.critical_combat.shared;

import java.util.Collection;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.mixin.minecraft.shared.LivingEntityMixin;
import dev.nekotune.critical_combat.mixin.minecraft.shared.PlayerMixin;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import dev.nekotune.critical_combat.util.TickDataTracker;
import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

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
    @SuppressWarnings("unchecked")
    public static CCPlayerExtension get(final Player player) {
        return ((MixinExtension<CCPlayerExtension>) player).critical_combat$getExtension();
    }

    public final Player player;
    public final CameraTracker cameraTracker;
    public final SlashTracker slashTracker = new SlashTracker();
    private float attackHeat = 0f;
    private int combatWearCooldownTicks = 0;
    private int slashFXCooldownTicks = 0;

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

        this.combatWearCooldownTicks = Math.max(this.combatWearCooldownTicks - 1, 0);
        this.slashFXCooldownTicks = Math.max(this.slashFXCooldownTicks - 1, 0);
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
     * @see LivingEntityMixin#critical_combat$hookSwing
     */
    public void swing(final InteractionHand hand, final CallbackInfo ci) {
        final ItemStack heldItem = this.player.getItemInHand(hand);
        final @Nullable WeaponType heldWeaponType = WeaponTypes.get(heldItem.getItem());
        if (hand == InteractionHand.MAIN_HAND) {
            if (heldWeaponType != null) {
                this.slashTracker.update(this.player.level().getGameTime());
                if (this.slashFXCooldownTicks == 0) {
                    this.playSlashFX(heldWeaponType);
                    this.slashFXCooldownTicks = 4;
                }
            }
        }
    }

    /**
     * Plays slashing particles and sounds in front of the player.
     * 
     * @param weaponType The WeaponType whose particles/sounds should be used
     */
    public void playSlashFX(final WeaponType weaponType) {
        final Level level = this.player.level();
        final Vec3 look = this.player.getLookAngle();
        final double x = this.player.getX() + look.x * 0.8;
        final double y = this.player.getEyeY() + look.y * 0.8 - 0.2;
        final double z = this.player.getZ() + look.z * 0.8;
        final @Nullable ParticleType<SlashParticleOptions> particleType = weaponType.slashParticle();
        final @Nullable SoundEvent soundEvent = weaponType.slashSound();
        if (particleType != null) {
            final boolean flipped = this.slashTracker.isSlashFlipped();
            final float pitch = Mth.wrapDegrees(this.player.getXRot() + 90f);
            final float yaw = this.player.getYRot();
            final float roll = (float) level.getRandom().triangle(0D, 10D);
            final float scale = (float) level.getRandom().triangle(1D, 0.1D);
            final var particleOptions = new SlashParticleOptions(particleType,
                    this.player.getId(), flipped, yaw, pitch, roll, scale);
            if (level instanceof final ServerLevel serverLevel) {
                serverLevel.players().forEach(serverPlayer -> {
                    if (serverPlayer != this.player) {
                        serverLevel.sendParticles(serverPlayer, particleOptions, false, x, y, z,
                                0, 0D, 0D, 0D, 0D);
                    }
                });
            } else if (this.player.isLocalPlayer()) {
                level.addParticle(particleOptions, x, y, z, 0D, 0D, 0D);
            }
        }
        if (soundEvent != null) {
            level.playSound(this.player, x, y, z, soundEvent,
                    this.player.getSoundSource(),
                    0.25f, 1.3f + level.getRandom().nextFloat() * 0.2f);
        }
    }

    /**
     * @return The player's current "heat" (% damage with LIGHT-type weapons)
     */
    public float getHeat() {
        return this.attackHeat;
    }

    /**
     * Starts a cooldown preventing the player's items from losing durability
     * when attacking enemies.
     * 
     * @param cooldownTicks Number of ticks to prevent durability damage for
     */
    public void setCombatWearCooldown(final int cooldownTicks) {
        this.combatWearCooldownTicks = cooldownTicks;
    }

    /**
     * @return True if items should be able to take durability damage from attacking
     *         enemies (if {@link CCPlayerExtension#combatWearCooldownTicks} == 0)
     */
    public boolean shouldApplyCombatWear() {
        return this.combatWearCooldownTicks == 0;
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

    public static final class SlashTracker {
        private static final int RAPID_SWING_WINDOW_TICKS = 10;

        private long lastSlashTick = -RAPID_SWING_WINDOW_TICKS - 1L;
        private boolean isSlashFlipped = false;

        public boolean isSlashFlipped() {
            return this.isSlashFlipped;
        }

        public void update(final long now) {
            this.isSlashFlipped = (now - this.lastSlashTick <= RAPID_SWING_WINDOW_TICKS)
                    && !this.isSlashFlipped();
            this.lastSlashTick = now;
        }
    }
}
