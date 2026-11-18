package dev.nekotune.critical_combat.client;

import java.util.function.Consumer;

import dev.nekotune.critical_combat.platform.PlatformNetwork$Fabric;
import dev.nekotune.critical_combat.platform.PlatformNetwork$Fabric.PayloadHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class CriticalCombatClient$Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PlatformNetwork$Fabric.get().onPayloadRegistered
                .subscribe(CriticalCombatClient$Fabric::registerPayload);
        CriticalCombatClient.init();
    }

    private static <T extends CustomPacketPayload> void registerPayload(final PayloadHandler<T> callback) {
        final Consumer<T> handler = callback.handler();
        ClientPlayNetworking.registerGlobalReceiver(callback.payload(),
                (payload, context) -> handler.accept(payload));
    }
}
