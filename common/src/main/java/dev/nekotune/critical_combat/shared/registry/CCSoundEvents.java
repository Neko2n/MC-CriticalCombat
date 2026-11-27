package dev.nekotune.critical_combat.shared.registry;

import java.util.function.Supplier;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.RegistryProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * Sounds registry for {@link CriticalCombat}
 */
public interface CCSoundEvents {

    public static final RegistryProvider<SoundEvent> SOUND_EVENTS = Platform.get()
            .openRegistry(() -> BuiltInRegistries.SOUND_EVENT);

    /** Attack sound for DEFAULT-type weapons */
    public static final Supplier<SoundEvent> DEFAULT_ATTACK = define("default_attack");

    /** Attack sound for LIGHT-type weapons */
    public static final Supplier<SoundEvent> LIGHT_ATTACK = define("light_attack");

    /** Attack sound for BLADE-type weapons */
    public static final Supplier<SoundEvent> BLADE_ATTACK = define("blade_attack");

    /** Sound for when you block an attack with a BLADE-type weapon */
    public static final Supplier<SoundEvent> BLADE_BLOCK = define("blade_block");

    /** Attack sound for POLEARM-type weapons */
    public static final Supplier<SoundEvent> POLEARM_ATTACK = define("polearm_attack");

    /** Hit sound for POLEARM-type weapons */
    public static final Supplier<SoundEvent> POLEARM_HIT = define("polearm_hit");

    private static Supplier<SoundEvent> define(final String name) {
        final ResourceLocation location = ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, name);
        return SOUND_EVENTS.define(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
}
