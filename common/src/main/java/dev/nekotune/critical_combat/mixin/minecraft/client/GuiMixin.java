package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.critical_combat.client.gui.CCCrosshair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(value = Gui.class)
public abstract class GuiMixin implements CCCrosshair.MixinHolder {

    @Unique
    private CCCrosshair critical_combat$crosshair;

    @Override
    public CCCrosshair critical_combat$getCrosshair() {
        return this.critical_combat$crosshair;
    }

    /**
     * Initialize the custom crosshair.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void critical_combat$initCrosshair(final Minecraft minecraft, final CallbackInfo ci) {
        this.critical_combat$crosshair = new CCCrosshair(minecraft, (Gui) (Object) this);
    }

    /**
     * Replace the game's crosshair with a custom one.
     */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void critical_combat$replaceCrosshair(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker, final CallbackInfo ci) {
        this.critical_combat$crosshair.render(guiGraphics, deltaTracker);
        ci.cancel();
    }
}
