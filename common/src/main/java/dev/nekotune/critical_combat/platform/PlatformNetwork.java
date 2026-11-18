package dev.nekotune.critical_combat.platform;

import java.util.function.Consumer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformNetwork {

    public abstract <T extends CustomPacketPayload> void register$S2C(final CustomPacketPayload.Type<T> payload, final StreamCodec<? super RegistryFriendlyByteBuf, T> codec, final Consumer<T> handler);

    public abstract void sendToClient(final ServerPlayer player, final CustomPacketPayload payload);
}
