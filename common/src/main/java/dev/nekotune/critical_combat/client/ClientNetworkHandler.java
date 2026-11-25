package dev.nekotune.critical_combat.client;

import javax.annotation.Nullable;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.shared.SwingEffects;
import dev.nekotune.critical_combat.shared.SwingEffects.SwingFXBroadcastPayload;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.MappingsBroadcastPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public interface ClientNetworkHandler {
    
    public static void registerPayloadHandlers() {
        Platform.get().network().register$S2C(MappingsBroadcastPayload.TYPE, MappingsBroadcastPayload.STREAM_CODEC,
                ClientNetworkHandler::handleMappingsBroadcastPayload);
        Platform.get().network().register$S2C(SwingFXBroadcastPayload.TYPE, SwingFXBroadcastPayload.STREAM_CODEC,
                ClientNetworkHandler::handleSwingFXBroadcastPayload);
    }

    private static void handleMappingsBroadcastPayload(final MappingsBroadcastPayload payload) {
        CriticalCombat.LOG.debug("Received WeaponType mappings payload: {}", payload.mappings());
        WeaponTypes.setMappings(payload.mappings());
    }

    private static void handleSwingFXBroadcastPayload(final SwingFXBroadcastPayload payload) {
        final @Nullable ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            final @Nullable Entity entity = level.getEntity(payload.entityId());
            if (entity != null) {
                SwingEffects.play(entity, payload.weaponType());
            }
        }
    }
}
