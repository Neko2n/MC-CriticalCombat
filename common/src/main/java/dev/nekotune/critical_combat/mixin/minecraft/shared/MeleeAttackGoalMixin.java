package dev.nekotune.critical_combat.mixin.minecraft.shared;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * Modifies how mobs attack depending on the type of weapon they're holding.
 */
@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin {

    private static final float LIGHT_WEAPON_COOLDOWN_DIVISOR = 2f;

    @Shadow
    @Final
    protected PathfinderMob mob;
    @Shadow
    private int ticksUntilNextAttack;

    /**
     * Make mobs attack twice as fast when they're using a LIGHT-type weapon.
     * 
     * @see MeleeAttackGoalMixin#critical_combat$fasterLightWeaponAttacks(CallbackInfoReturnable)
     */
    @Inject(method = "resetAttackCooldown", at = @At("TAIL"))
    private void critical_combat$fasterLightWeaponAttacks(final CallbackInfo ci) {
        if (WeaponTypes.get(this.mob.getWeaponItem().getItem()) == WeaponType.LIGHT) {
            this.ticksUntilNextAttack /= LIGHT_WEAPON_COOLDOWN_DIVISOR;
        }
    }

    /**
     * Make mobs attack twice as fast when they're using a LIGHT-type weapon.
     * 
     * @see MeleeAttackGoalMixin#critical_combat$fasterLightWeaponAttacks(CallbackInfo)
     */
    @Inject(method = "getAttackInterval", at = @At("RETURN"))
    private void critical_combat$fasterLightWeaponAttacks(final CallbackInfoReturnable<Integer> cir) {
        if (WeaponTypes.get(this.mob.getWeaponItem().getItem()) == WeaponType.LIGHT) {
            cir.setReturnValue(Math.round(cir.getReturnValue() / LIGHT_WEAPON_COOLDOWN_DIVISOR));
        }
    }
}
