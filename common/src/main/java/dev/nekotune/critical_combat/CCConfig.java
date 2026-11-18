package dev.nekotune.critical_combat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CCConfig {

    /** Registered as {@code ModConfig.Type.COMMON} */
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue PROCEDURAL_WEAPON_TYPES;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        PROCEDURAL_WEAPON_TYPES = builder
                .comment(
                        "Whether items should be procedurally given weapon types. Set to false to only assign weapon types manually through datapacks.")
                .translation("config.critical_combat.procedural_weapon_types")
                .define("procedural_weapon_types", true);

        SPEC = builder.build();
    }
}
