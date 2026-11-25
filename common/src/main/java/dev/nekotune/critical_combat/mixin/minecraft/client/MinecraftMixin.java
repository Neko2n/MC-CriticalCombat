package dev.nekotune.critical_combat.mixin.minecraft.client;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.shared.CCPlayer_;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.HitResult;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {

    /**
     * Block attacks while on cooldown.
     */
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void critical_combat$blockCooldownAttacks(final CallbackInfoReturnable<Boolean> cir) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = Objects.requireNonNull(minecraft.player);
        if (!CCConfig.CAN_ATTACK_ON_COOLDOWN.get()
                && player.getAttackStrengthScale(0f) < 0.8f) {
            cir.setReturnValue(false);
            return;
        }
    }

    /**
     * Hook up {@link CCPlayer_#swing}
     */
    @Inject(method = "startAttack", at = @At("RETURN"))
    private void critical_combat$hookSwing(final CallbackInfoReturnable<Boolean> cir) {
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = Objects.requireNonNull(minecraft.player);
        final CCPlayer_ ccplayer = CCPlayer_.get(player);
        final boolean isMining = minecraft.hitResult != null
                && minecraft.hitResult.getType() == HitResult.Type.BLOCK;
        ccplayer.swing(isMining, cir);
    }
}
