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
public final class CCGuiExtension {

    public final Minecraft minecraft;

    /**
     * Custom crosshair
     */
    public final CCCrosshair crosshair;

    /**
     * Returns the extension associated with the given GUI.
     * 
     * @param gui The GUI to get the extension for
     * @return CCGuiExtension object for the given GUI
     */
    @SuppressWarnings("unchecked")
    public static CCGuiExtension get(final Gui gui) {
        return ((MixinExtension<CCGuiExtension>) gui).critical_combat$getExtension();
    }

    /**
     * @see GuiMixin#critical_combat$initGuiExtension
     */
    public CCGuiExtension(final Minecraft minecraft, final Gui gui) {
        this.minecraft = minecraft;
        this.crosshair = new CCCrosshair(minecraft, gui);
    }
}
