package dev.nekotune.critical_combat.shared.registry;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.RegistryProvider;
import dev.nekotune.critical_combat.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

/**
 * Items registry for {@link CriticalCombat}
 */
public interface CCItems {

    public static final RegistryProvider<Item> ITEMS = Platform.get()
            .openRegistry(() -> BuiltInRegistries.ITEM);
}
