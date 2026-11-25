package dev.nekotune.critical_combat.mixin.minecraft.shared;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.shared.CCPlayer_;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * Modifies the knockback applied by LIGHT-type weapons.
     * Factors in the player's "heat" + whether the weapon critically hit.
     */
    @Inject(method = "getKnockback", at = @At("RETURN"), cancellable = true)
    private void critical_combat$modifyAttackKnockback(final Entity attacker, final DamageSource damageSource,
            final CallbackInfoReturnable<Float> cir) {
        final LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (attacker instanceof final Player player) {
            final @Nullable WeaponType heldWeaponType = WeaponTypes.get(player.getWeaponItem().getItem());
            if (heldWeaponType == WeaponType.LIGHT) {
                final CCPlayer_ ext = CCPlayer_.get(player);
                final float maxKbModifier = heldWeaponType.shouldCrit(player, livingEntity) ? 1.5f : 1f;
                final float kbModifier = Mth.map(ext.getHeat(), 0f, 1f, 0.5f, maxKbModifier);
                cir.setReturnValue(cir.getReturnValue() * kbModifier);
            }
        }
    }

    /**
     * Modifies damage taken from LIGHT-type weapons to scale with the player's
     * "heat".
     */
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float critical_combat$modifyAttackDamage(final float amount,
            final DamageSource source) {
        if (source.getEntity() instanceof final Player player) {
            final @Nullable ItemStack weaponItem = source.getWeaponItem();
            if (weaponItem != null) {
                final WeaponType heldWeaponType = WeaponTypes.get(weaponItem.getItem());
                if (heldWeaponType == WeaponType.LIGHT) {
                    final CCPlayer_ ext = CCPlayer_.get(player);
                    final float minDmgScale = CCConfig.HEAT_DAMAGE_SCALE.get().floatValue();
                    return amount * Mth.map(ext.getHeat(), 0f, 1f, minDmgScale, 1f);
                }
            }
        }
        return amount;
    }

    /**
     * Reduce invulnerability time when damaged by LIGHT-type weapons.
     * Scales with the attack speed attribute, from 5 frames at 2.0 attack speed to
     * 1 frame at 4.0 attack speed.
     */
    @Inject(method = "hurt", at = @At("RETURN"))
    private void critical_combat$modifyInvulnerableTime(final DamageSource source, final float amount,
            final CallbackInfoReturnable<Boolean> cir) {
        final LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (cir.getReturnValue() && livingEntity.invulnerableTime == 20) {
            final @Nullable ItemStack usedWeaponItem = source.getWeaponItem();
            if (usedWeaponItem != null) {
                final WeaponType usedWeaponType = WeaponTypes.get(usedWeaponItem.getItem());
                if (usedWeaponType == WeaponType.LIGHT) {
                    final double attackSpeed;
                    if (source.getEntity() instanceof final LivingEntity attacker) {
                        attackSpeed = Math.max(2D, attacker.getAttributeValue(Attributes.ATTACK_SPEED));
                    } else {
                        attackSpeed = 4D;
                    }
                    final long iframes = Math.round(Mth.map(attackSpeed, 2D, 4D, 5D, 1D));
                    livingEntity.invulnerableTime = 10 + (int) iframes;
                }
            }
        }
    }

    @ModifyConstant(method = "getCurrentSwingDuration", constant = @Constant(intValue = 6))
    private int critical_combat$overrideDefaultSwingDuration(final int original) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        final ItemStack stack = entity.getMainHandItem();
        if (entity instanceof final Player player) {
            if (WeaponTypes.get(stack.getItem()) == WeaponType.POLEARM) {
                return Math.max(1, Math.round(player.getCurrentItemAttackStrengthDelay()));
            }
        }
        return original;
    }
}
