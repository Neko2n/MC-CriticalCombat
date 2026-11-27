package dev.nekotune.critical_combat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CCConfig {

    /** Registered as {@code ModConfig.Type.COMMON} */
    public static final ModConfigSpec SPEC;

    /** Whether or not weapon types should be procedurally assigned. */
    public static final ModConfigSpec.BooleanValue PROCEDURAL_WEAPON_TYPES;

    /** The speed threshold (upper-bound) for weapons to be two-handed. */
    public static final ModConfigSpec.ConfigValue<Double> TWO_HANDED_SPEED;

    /**
     * The camera/movement speed required for a BLADE-type weapon to critically hit.
     */
    public static final ModConfigSpec.ConfigValue<Double> BLADE_CRIT_SPEED;

    /**
     * Whether LIGHT-type weapons should use 1.8-inspired combat. (no attack
     * cooldown)
     */
    public static final ModConfigSpec.BooleanValue LIGHT_REMOVE_ATTACK_COOLDOWN;

    /**
     * The amount of "heat" you gain with each successful hit (damage % for LIGHT
     * weapons)
     */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_PER_HIT;

    /**
     * The rate per second at which your "heat" drains (damage % for LIGHT weapons)
     */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_DRAIN;

    /** The % of damage LIGHT-type weapons deal when at 0 heat. */
    public static final ModConfigSpec.ConfigValue<Double> HEAT_DAMAGE_SCALE;

    /** Whether heat should reset when you take damage. */
    public static final ModConfigSpec.BooleanValue HEAT_RESETS_ON_HURT;

    /**
     * The number of seconds after attacking that durability loss from attacks
     * should be disabled.
     */
    public static final ModConfigSpec.ConfigValue<Double> COMBAT_WEAR_COOLDOWN;

    /** Whether you should be able to attack during your attack cooldown. */
    public static final ModConfigSpec.BooleanValue CAN_ATTACK_ON_COOLDOWN;

    /** Whether sounds should play when swinging weapons */
    public static final ModConfigSpec.BooleanValue SWING_SOUNDS;

    /** Whether swinging weapons should play a particle animation */
    public static final ModConfigSpec.BooleanValue SWING_PARTICLES;

    /** Whether you should be able to block with swords */
    public static final ModConfigSpec.BooleanValue BLADE_BLOCK_ENABLED;

    /** Whether sword blocking should have a cooldown after any hit, or only axes */
    public static final ModConfigSpec.BooleanValue BLADE_WEAK_BLOCK;

    /** How long you should be able to hold down block with a sword */
    public static final ModConfigSpec.ConfigValue<Double> BLADE_BLOCK_TIMER;

    /** How long you should be able to hold down block with a shield */
    public static final ModConfigSpec.ConfigValue<Double> SHIELD_BLOCK_TIMER;

    /** How many seconds it should take to regenerate your block timer */
    public static final ModConfigSpec.ConfigValue<Double> BLOCK_REGEN_TIMER;

    /** Whether axes should disable blocking, or only HEAVY-type weapons */
    public static final ModConfigSpec.BooleanValue AXES_DISABLE_BLOCKING;

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
                .defineInRange("heat_per_hit", 0.1D, 0D, 1D);

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

        CAN_ATTACK_ON_COOLDOWN = builder
                .translation("config.critical_combat.can_attack_on_cooldown")
                .define("can_attack_on_cooldown", false);

        SWING_SOUNDS = builder
                .translation("config.critical_combat.swing_sounds")
                .define("swing_sounds", true);

        SWING_PARTICLES = builder
                .translation("config.critical_combat.swing_particles")
                .define("swing_particles", true);

        BLADE_BLOCK_ENABLED = builder
                .translation("config.critical_combat.blade_block_enabled")
                .define("blade_block_enabled", true);

        BLADE_WEAK_BLOCK = builder
                .translation("config.critical_combat.blade_weak_block")
                .define("blade_weak_block", true);

        BLADE_BLOCK_TIMER = builder
                .translation("config.critical_combat.blade_block_timer")
                .defineInRange("blade_block_timer", 0.5D, 0D, Float.MAX_VALUE);

        SHIELD_BLOCK_TIMER = builder
                .translation("config.critical_combat.shield_block_timer")
                .defineInRange("shield_block_timer", 0D, 0D, Float.MAX_VALUE);

        BLOCK_REGEN_TIMER = builder
                .translation("config.critical_combat.block_regen_timer")
                .defineInRange("block_regen_timer", 1D, 0D, Float.MAX_VALUE);

        AXES_DISABLE_BLOCKING = builder
                .translation("config.critical_combat.axes_disable_blocking")
                .define("axes_disable_blocking", true);

        SPEC = builder.build();
    }
}
