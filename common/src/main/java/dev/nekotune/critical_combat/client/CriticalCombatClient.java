package dev.nekotune.critical_combat.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import dev.nekotune.critical_combat.client.definition.LightSlashParticle;
import dev.nekotune.critical_combat.client.definition.SlashParticle;
import dev.nekotune.critical_combat.platform.client.ClientPlatform;
import dev.nekotune.critical_combat.platform.client.ClientPlatformEvents.RegisterParticleProvidersEvent$Payload;
import dev.nekotune.critical_combat.shared.registry.CCParticleTypes;

public final class CriticalCombatClient {

    private static final CompletableFuture<Void> MOD_LOADED = new CompletableFuture<>();

    public static CompletionStage<Void> onModLoaded() {
        return MOD_LOADED.minimalCompletionStage();
    }

    public static void init() {

        // Register client network payload handlers
        ClientNetworkHandler.registerPayloadHandlers();

        // Register particle providers
        ClientPlatform.get().events().onRegisterParticleProvidersEvent()
                .subscribe(CriticalCombatClient::registerParticleProviders);

        MOD_LOADED.complete(null);
    }

    private static void registerParticleProviders(final RegisterParticleProvidersEvent$Payload payload) {
        payload.register(CCParticleTypes.LIGHT_SWING.get(),
                sprites -> new LightSlashParticle.Provider(sprites, 0.4f, 4));
        payload.register(CCParticleTypes.BLADE_SWING.get(),
                sprites -> new SlashParticle.Provider(sprites, 0.4f, 4));
        payload.register(CCParticleTypes.POLEARM_SWING.get(),
                sprites -> new SlashParticle.Provider(sprites, 0.4f, 4));
    }

    private CriticalCombatClient() {
    }
}
