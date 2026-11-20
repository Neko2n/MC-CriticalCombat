package dev.nekotune.critical_combat.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import dev.nekotune.critical_combat.client.definition.SlashParticle;
import dev.nekotune.critical_combat.platform.client.ClientPlatform;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.registry.CCParticleTypes;

public final class CriticalCombatClient {

    private static final CompletableFuture<Void> MOD_LOADED = new CompletableFuture<>();

    public static CompletionStage<Void> onModLoaded() {
        return MOD_LOADED.minimalCompletionStage();
    }

    public static void init() {

        // Register client network payload receivers
        WeaponTypes.registerPayload();

        // Register particle providers
        ClientPlatform.get().events().onRegisterParticleProvidersEvent().subscribe(payload -> {
            payload.register(CCParticleTypes.LIGHT_SLASH.get(),
                    sprites -> new SlashParticle.Provider(sprites, 0.4f, 4));
        });

        MOD_LOADED.complete(null);
    }

    private CriticalCombatClient() {
    }
}
