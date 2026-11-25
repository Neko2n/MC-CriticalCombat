package dev.nekotune.critical_combat.client.gui;

import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.mixin.minecraft.client.GuiMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * Extends the functionality of the Gui
 * 
 * @see GuiMixin
 */
public final class CCGui_ {

    /**
     * Returns the extension associated with the given GUI.
     * 
     * @param gui The GUI to get the extension for
     * @return CCGui extension for the given GUI
     */
    @SuppressWarnings("unchecked")
    public static CCGui_ get(final Gui gui) {
        return ((MixinExtension<CCGui_>) gui).critical_combat$getExtension();
    }

    public final Minecraft minecraft;
    public final Gui gui;

    /**
     * Custom crosshair
     */
    public final CCCrosshair crosshair;

    /**
     * @see GuiMixin#critical_combat$initGuiExtension
     */
    public CCGui_(final Minecraft minecraft, final Gui gui) {
        this.minecraft = minecraft;
        this.gui = gui;
        this.crosshair = new CCCrosshair(minecraft, gui);
    }
}
