package dev.nekotune.critical_combat.shared;

import java.util.Collection;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.mixin.minecraft.client.ItemInHandRendererMixin;
import dev.nekotune.critical_combat.mixin.minecraft.client.MinecraftMixin;
import dev.nekotune.critical_combat.mixin.minecraft.shared.PlayerMixin;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import dev.nekotune.critical_combat.util.SoundOptions;
import dev.nekotune.critical_combat.util.TickDataTracker;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

/**
 * Extended logic appended to Player entities.
 * 
 * @see PlayerMixin
 */
@ParametersAreNonnullByDefault
public final class CCPlayer_ {

    /**
     * Returns the extension associated with the given player.
     * 
     * @param player The player to get the extension for
     * @return CCPlayer extension for the given player
     */
    @SuppressWarnings("unchecked")
    public static CCPlayer_ get(final Player player) {
        return ((MixinExtension<CCPlayer_>) player).critical_combat$getExtension();
    }

    public final Player player;
    public final CameraTracker cameraTracker;
    public final SlashTracker slashTracker = new SlashTracker();
    private float attackHeat = 0f;
    private int combatWearCooldownTicks = 0;
    private int swingFXCooldownTicks = 0;
    public int swapTicks = 0;
    private ItemStack previousHeldItem = ItemStack.EMPTY;

    public CCPlayer_(final Player player) {
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
        this.swingFXCooldownTicks = Math.max(this.swingFXCooldownTicks - 1, 0);

        this.tickSwapItem();
    }

    /**
     * @see ItemInHandRendererMixin#critical_combat$useItemSwapScale
     */
    private void tickSwapItem() {
        final ItemStack heldItem = this.player.getMainHandItem();
        if (ItemStack.isSameItem(heldItem, this.previousHeldItem)) {
            this.swapTicks++;
        } else {
            this.swapTicks = 0;
        }
        this.previousHeldItem = heldItem;
    }

    /**
     * @see PlayerMixin#critical_combat$hookAttack
     */
    public void attack(final Entity target, final CallbackInfo ci) {
        if (!CCConfig.CAN_ATTACK_ON_COOLDOWN.get()
                && this.player.getAttackStrengthScale(0f) < 0.8f) {
            ci.cancel();
            return;
        }

        final boolean canAttack = target.isAttackable()
                && !target.skipAttackInteraction(this.player);
        if (canAttack) {
            if (target instanceof LivingEntity) {
                final @Nullable WeaponType heldWeaponType = WeaponTypes.get(this.player.getWeaponItem().getItem());
                if (heldWeaponType != null) {

                    // Accrue heat when attacking mobs with LIGHT-weapons
                    if (heldWeaponType == WeaponType.LIGHT) {
                        float heatGain = CCConfig.HEAT_PER_HIT.get().floatValue();
                        if (heldWeaponType.shouldCrit(this.player, target))
                            heatGain *= 2f;
                        this.attackHeat = Math.clamp(this.attackHeat + heatGain, 0f, 1f);
                    }

                    // Play weapon-specific hit sounds
                    final @Nullable SoundOptions hitSound = heldWeaponType.swingEffects.hitSound();
                    if (hitSound != null) {
                        hitSound.playSound(this.player.level(), player,
                                target.getX(), target.getY(), target.getZ(),
                                player.getSoundSource());
                    }
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
     * @see MinecraftMixin#critical_combat$hookSwing
     */
    public void swing(final boolean isMining, final CallbackInfoReturnable<Boolean> cir) {
        final ItemStack heldItem = this.player.getMainHandItem();
        final @Nullable WeaponType heldWeaponType = WeaponTypes.get(heldItem.getItem());
        if (heldWeaponType != null && !isMining) {
            if (this.swingFXCooldownTicks == 0) {
                final Level level = this.player.level();
                this.slashTracker.update(level.getGameTime());
                SwingEffects.play(this.player, heldWeaponType);
                final boolean isLocalClient = this.player.isLocalPlayer() && level.isClientSide();
                this.swingFXCooldownTicks = isLocalClient ? 1 : 4;
            }
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
     *         enemies (if {@link CCPlayer_#combatWearCooldownTicks} == 0)
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
