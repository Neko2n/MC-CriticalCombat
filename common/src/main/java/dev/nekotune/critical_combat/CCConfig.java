package dev.nekotune.critical_combat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CCConfig {

    /** Registered as {@code ModConfig.Type.COMMON} */
    public static final ModConfigSpec SPEC;

    /** Whether or not weapon types should be procedurally assigned. */
    public static final ModConfigSpec.BooleanValue PROCEDURAL_WEAPON_TYPES;

    /** The speed threshold (upper-bound) for weapons to be two-handed. */
    public static final ModConfigSpec.ConfigValue<Double> TWO_HANDED_SPEED;

    /** The camera/movement speed required for a BLADE-type weapon to critically hit. */
    public static final ModConfigSpec.ConfigValue<Double> BLADE_CRIT_SPEED;

    /** Whether LIGHT-type weapons should use 1.8-inspired combat. (no attack cooldown) */
    public static final ModConfigSpec.BooleanValue LIGHT_REMOVE_ATTACK_COOLDOWN;

    /** The amount of "heat" you gain with each successful hit (damage % for LIGHT weapons) */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_PER_HIT;

    /** The rate per second at which your "heat" drains (damage % for LIGHT weapons) */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_DRAIN;

    /** The % of damage LIGHT-type weapons deal when at 0 heat. */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_DAMAGE_SCALE;

    /** Whether heat should reset when you take damage. */
    public static final ModConfigSpec.BooleanValue HEAT_RESETS_ON_HURT;

    /** The number of seconds after attacking that durability loss from attacks should be disabled. */
    public static final ModConfigSpec.ConfigValue<Double> COMBAT_WEAR_COOLDOWN;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        PROCEDURAL_WEAPON_TYPES = builder
                .translation("config.critical_combat.procedural_weapon_types")
                .define("procedural_weapon_types", true);

        TWO_HANDED_SPEED = builder
                .translation("config.critical_combat.two_handed_speed")
                .defineInRange("two_handed_speed", 1D, 0D, 4D);

        BLADE_CRIT_SPEED = builder
                .translation("config.critical_combat.blade_crit_speed")
                .defineInRange("blade_crit_speed", 10D, 0D, Double.MAX_VALUE);

        LIGHT_REMOVE_ATTACK_COOLDOWN = builder
                .translation("config.critical_combat.light_remove_attack_cooldown")
                .define("light_remove_attack_cooldown", true);

        HEAT_PER_HIT = builder
                .translation("config.critical_combat.heat_per_hit")
                .defineInRange("heat_per_hit", 0.2D, 0D, 1D);

        HEAT_DRAIN = builder
                .translation("config.critical_combat.heat_drain")
                .define("heat_drain", 0.5D);

        HEAT_DAMAGE_SCALE = builder
                .translation("config.critical_combat.heat_damage_scale")
                .defineInRange("heat_damage_scale", 0.5D, 0D, 1D);

        HEAT_RESETS_ON_HURT = builder
                .translation("config.critical_combat.heat_resets_on_hurt")
                .define("heat_resets_on_hurt", true);

        COMBAT_WEAR_COOLDOWN = builder
                .translation("config.critical_combat.combat_wear_cooldown")
                .defineInRange("combat_wear_cooldown", 0.5D, 0D, Float.MAX_VALUE);

        SPEC = builder.build();
    }
}
