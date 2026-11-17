package dev.nekotune.critical_combat.shared.registry;

public interface ModRegistries {

    public static void registerAll() {
        CCItems.ITEMS.register();
    }
}
