package dev.nekotune.critical_combat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.server.CriticalCombatServer;
import dev.nekotune.critical_combat.shared.registry.ModRegistries;

public final class CriticalCombat {
    public static final String MOD_ID = "critical_combat";
    public static final String MOD_NAME = "Critical Combat";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private static final CompletableFuture<Void> MOD_LOADED = new CompletableFuture<>();
    public static CompletionStage<Void> onModLoaded() {
        return MOD_LOADED.minimalCompletionStage();
    }

    public static void init() {
        ModRegistries.registerAll();
        CriticalCombatServer.init();

        Platform.get().events().onRegisterCommandsEvent().subscribe(payload -> {
            CC$Commands.register(payload.dispatcher(), payload.context());
        });

        MOD_LOADED.complete(null);
    }

    private CriticalCombat() {
    }
}
