package dev.nekotune.critical_combat.platform.client;

import dev.nekotune.critical_combat.platform.client.ClientPlatform.IClientPlatformHelper;

public final class ClientPlatformHelper$Fabric implements IClientPlatformHelper {

    @Override
    public ClientPlatformEvents events() {
        return ClientPlatformEvents$Fabric.get();
    }
}
