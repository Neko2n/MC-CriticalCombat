package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.critical_combat.client.gui.CCCrosshair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(value = Gui.class)
public abstract class GuiMixin {

    /**
     * Replace the game's crosshair with a custom one driven by the mod.
     */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void critical_combat$replaceCrosshair(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker, final CallbackInfo ci) {
        CCCrosshair.render((Gui) (Object) this, guiGraphics, deltaTracker);
        ci.cancel();
    }
}
