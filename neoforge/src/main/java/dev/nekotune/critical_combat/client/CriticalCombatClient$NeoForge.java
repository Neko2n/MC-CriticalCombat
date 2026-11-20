package dev.nekotune.critical_combat.client;

import dev.nekotune.critical_combat.CriticalCombat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CriticalCombat.MOD_ID, dist = Dist.CLIENT)
public class CriticalCombatClient$NeoForge {

    public CriticalCombatClient$NeoForge(final IEventBus eventBus, final ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        CriticalCombatClient.init();
    }
}
