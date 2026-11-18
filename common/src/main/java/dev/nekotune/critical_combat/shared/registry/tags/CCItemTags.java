package dev.nekotune.critical_combat.shared.registry.tags;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Custom item tags for {@link CriticalCombat}
 */
public class CCItemTags {

    /**
     * Items which have +50% innate armor piercing.
     * Typically for pickaxes.
     */
    public static final TagKey<Item> PIERCES_ARMOR = define("pierces_armor");

    /**
     * Items which deal upwards knockback instead of forwards knockback.
     * Typically for shovels.
     */
    public static final TagKey<Item> UPWARDS_KNOCKBACK = define("upwards_knockback");

    private static final TagKey<Item> define(final String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, name));
    }
}
