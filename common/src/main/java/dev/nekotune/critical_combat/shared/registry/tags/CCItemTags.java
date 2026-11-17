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
     * Item tags for weapon items.
     */
    public static interface Weapon {

        /**
         * Weapons that behave as default vanilla weapons.
         * Crits when falling.
         */
        public static final TagKey<Item> DEFAULT = define("weapon/default");

        /**
         * Weapons that require charging up to swing and mine, and do innate AoE damage.
         * Crits when charged to 200%.
         * Typically for hammer-like weapons.
         */
        public static final TagKey<Item> HEAVY = define("weapon/heavy");

        /**
         * Weapons that behave like 1.8, with no attack cooldown.
         * Crits when up-close.
         * Typically for knives.
         */
        public static final TagKey<Item> LIGHT = define("weapon/light");

        /**
         * Weapons that can parry projectiles with right-click, and do innate sweeping
         * damage.
         * Crits when the player's camera is moving fast.
         * Typically for swords.
         */
        public static final TagKey<Item> BLADE = define("weapon/blade");

        /**
         * Weapons that can be thrown with right-click.
         * Crits when the player is sprinting.
         * Typically for spear-like weapons.
         */
        public static final TagKey<Item> POLEARM = define("weapon/polearm");

        /**
         * Weapons which have +50% innate armor piercing.
         * Typically for pickaxes.
         */
        public static final TagKey<Item> PIERCES_ARMOR = define("weapon/pierces_armor");

        /**
         * Weapons which deal upwards knockback instead of forwards knockback.
         * Typically for shovels.
         */
        public static final TagKey<Item> UPWARDS_KNOCKBACK = define("weapon/upwards_knockback");
    }

    private static final TagKey<Item> define(final String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, name));
    }
}
