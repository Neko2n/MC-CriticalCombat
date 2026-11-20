package dev.nekotune.critical_combat.mixin.minecraft.shared;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.shared.CCPlayerExtension;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private @Nullable Player critical_combat$postHurtAttacker = null;

    /**
     * Add marker for the callstack being within {@link ItemStack#postHurtEnemy}
     */
    @Inject(method = "postHurtEnemy", at = @At("HEAD"))
    private void critical_combat$markAttackerForCombatWearCooldown(final LivingEntity target,
            final Player attacker, final CallbackInfo ci) {
        critical_combat$postHurtAttacker = attacker;
    }

    /**
     * Remove marker for the callstack being within {@link ItemStack#postHurtEnemy}
     */
    @Inject(method = "postHurtEnemy", at = @At("TAIL"))
    private void critical_combat$setCombatWearCooldown(final LivingEntity target,
            final Player attacker, final CallbackInfo ci) {
        critical_combat$postHurtAttacker = null;
    }

    /**
     * Cancel durability loss if called within {@link ItemStack#postHurtEnemy}
     * and combat wear is on cooldown.
     */
    @Inject(method = "hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V", at = @At("HEAD"), cancellable = true)
    private void critical_combat$applyCombatWearCooldown(final int damage, final LivingEntity entity,
            final EquipmentSlot slot, final CallbackInfo ci) {
        final ItemStack stack = (ItemStack) (Object) this;
        final @Nullable Player attacker = this.critical_combat$postHurtAttacker;
        if (attacker != null && damage > 0 && stack.isDamageableItem()) {
            final CCPlayerExtension ext = CCPlayerExtension.get(attacker);
            if (ext.shouldApplyCombatWear()) {
                final double baseCooldownSecs = CCConfig.COMBAT_WEAR_COOLDOWN.get();
                final double attackSpeed = attacker.getAttributeValue(Attributes.ATTACK_SPEED);
                double cooldownSecs = baseCooldownSecs / 2D;
                cooldownSecs = Math.min(baseCooldownSecs, cooldownSecs * attackSpeed);
                final int cooldownTicks = (int) Math.round(SharedConstants.TICKS_PER_SECOND * cooldownSecs);
                ext.setCombatWearCooldown(cooldownTicks);
            } else {
                ci.cancel();
            }
        }
    }
}
