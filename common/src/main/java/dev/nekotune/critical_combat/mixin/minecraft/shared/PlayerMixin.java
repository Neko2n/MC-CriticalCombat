package dev.nekotune.critical_combat.mixin.minecraft.shared;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.shared.CCPlayerExtension;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(value = Player.class)
public abstract class PlayerMixin implements MixinExtension<CCPlayerExtension> {

    @Unique
    private final CCPlayerExtension critical_combat$playerExtension = new CCPlayerExtension((Player) (Object) this);

    @Override
    public CCPlayerExtension critical_combat$getExtension() {
        return this.critical_combat$playerExtension;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void critical_combat$tickPlayerExtension(final CallbackInfo ci) {
        this.critical_combat$playerExtension.tick(ci);
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void critical_combat$hookAttack(final Entity target, final CallbackInfo ci) {
        this.critical_combat$playerExtension.attack(target, ci);
    }

    @Inject(method = "hurt", at = @At("RETURN"), cancellable = true)
    private void critical_combat$hookHurt(final DamageSource source, final float amount,
            final CallbackInfoReturnable<Boolean> cir) {
        this.critical_combat$playerExtension.hurt(source, amount, cir);
    }

    @Inject(method = "getCurrentItemAttackStrengthDelay", at = @At("HEAD"), cancellable = true)
    public void critical_combat$removeAttackCDForLightWeapons(final CallbackInfoReturnable<Float> cir) {
        final Player player = (Player) (Object) this;
        if (player.isHolding(stack -> WeaponTypes.get(stack.getItem()) == WeaponType.LIGHT)
                && CCConfig.LIGHT_REMOVE_ATTACK_COOLDOWN.get()) {
            cir.setReturnValue(Float.MIN_NORMAL);
        }
    }

    @ModifyVariable(method = "attack", ordinal = 2, at = @At(value = "STORE", ordinal = 0))
    private boolean critical_combat$changeCritCriteria(final boolean defaultCriteria, final Entity target) {
        final Player player = (Player) (Object) this;
        final @Nullable WeaponType weaponType = WeaponTypes.get(player.getWeaponItem().getItem());
        if (weaponType == null)
            return false; // Non-weapons can no longer crit
        return weaponType.shouldCrit(player, target);
    }
}
