package dev.nekotune.critical_combat.platform;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface PlatformNetwork {
    public <T extends CustomPacketPayload> void register$S2C(final CustomPacketPayload.Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final Consumer<T> handler);

    public <T extends CustomPacketPayload> void register$C2S(final CustomPacketPayload.Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final BiConsumer<T, ServerPlayer> handler);

    public void sendToClient(final ServerPlayer player, final CustomPacketPayload payload);

    public void sendToServer(final CustomPacketPayload payload);

    public void sendToClientsTrackingEntity(final Entity entity, final CustomPacketPayload payload);
}
