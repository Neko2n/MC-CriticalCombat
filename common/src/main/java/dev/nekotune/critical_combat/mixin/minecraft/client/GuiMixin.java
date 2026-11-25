package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.nekotune.critical_combat.client.gui.CC$Gui_;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(value = Gui.class)
public abstract class GuiMixin implements MixinExtension<CC$Gui_> {

    @Unique
    private CC$Gui_ critical_combat$guiExtension;

    @Override
    public CC$Gui_ critical_combat$getExtension() {
        return this.critical_combat$guiExtension;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void critical_combat$initGuiExtension(final Minecraft minecraft, final CallbackInfo ci) {
        this.critical_combat$guiExtension = new CC$Gui_(minecraft, (Gui) (Object) this);
    }

    /**
     * Replace the game's crosshair with a custom one.
     */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void critical_combat$replaceCrosshair(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker, final CallbackInfo ci) {
        this.critical_combat$getExtension().crosshair.render(guiGraphics, deltaTracker);
        ci.cancel();
    }
}
