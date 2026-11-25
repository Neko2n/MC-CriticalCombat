package dev.nekotune.critical_combat.shared.registry;

import java.util.function.Supplier;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.RegistryProvider;
import dev.nekotune.critical_combat.shared.definition.DaggerItem;
import dev.nekotune.critical_combat.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Items registry for {@link CriticalCombat}
 */
public final class CC$Items {

    public static final RegistryProvider<Item> ITEMS = Platform.get()
            .openRegistry(() -> BuiltInRegistries.ITEM);

    public static final Supplier<Item> BLACK_SHARD;
    public static final Supplier<DaggerItem> DAGGER;

    static {
        final Item.Properties blackShardProperties = new Item.Properties()
                .fireResistant()
                .rarity(Rarity.UNCOMMON);
        BLACK_SHARD = ITEMS.define("black_shard",
                () -> new Item(blackShardProperties));
        DAGGER = ITEMS.define("dagger",
                () -> new DaggerItem(blackShardProperties.attributes(DaggerItem
                        .createAttributes(2.5f, -1.8f))));
    }

    private CC$Items() {
    }
}
