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

        SPEC = builder.build();
    }
}
