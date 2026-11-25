package dev.nekotune.critical_combat.shared.registry;

public interface ModRegistries {

    public static void registerAll() {
        CC$Items.ITEMS.register();
        CC$SoundEvents.SOUND_EVENTS.register();
        CC$ParticleTypes.PARTICLE_TYPES.register();
    }
}
