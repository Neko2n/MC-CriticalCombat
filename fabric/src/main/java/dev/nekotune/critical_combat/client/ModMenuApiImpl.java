package dev.nekotune.critical_combat.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public final class ModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (final Screen modMenu) -> new ConfigurationScreen(CriticalCombat.MOD_ID, modMenu);
    }
}
