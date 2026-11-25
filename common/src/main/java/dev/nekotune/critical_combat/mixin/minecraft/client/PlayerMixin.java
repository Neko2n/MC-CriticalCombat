package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.nekotune.critical_combat.client.gui.CCGui_;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(value = Player.class)
public abstract class PlayerMixin {

    /**
     * Flash the attack indicator when swinging.
     */
    @Inject(method = "resetAttackStrengthTicker", at = @At(value = "HEAD"))
    private void critical_combat$flashOnSwing(final CallbackInfo ci) {
        if (!((Object) this instanceof LocalPlayer))
            return;
        final Gui gui = Minecraft.getInstance().gui;
        CCGui_.get(gui).crosshair.flashAttackIndicator();
    }
}
