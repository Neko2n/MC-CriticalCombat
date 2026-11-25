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
public final class CC$Gui_ {

    /**
     * Returns the extension associated with the given GUI.
     * 
     * @param gui The GUI to get the extension for
     * @return CC$Gui extension for the given GUI
     */
    @SuppressWarnings("unchecked")
    public static CC$Gui_ get(final Gui gui) {
        return ((MixinExtension<CC$Gui_>) gui).critical_combat$getExtension();
    }

    public final Minecraft minecraft;
    public final Gui gui;

    /**
     * Custom crosshair
     */
    public final CC$Crosshair crosshair;

    /**
     * @see GuiMixin#critical_combat$initGuiExtension
     */
    public CC$Gui_(final Minecraft minecraft, final Gui gui) {
        this.minecraft = minecraft;
        this.gui = gui;
        this.crosshair = new CC$Crosshair(minecraft, gui);
    }
}
