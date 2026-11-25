package dev.nekotune.critical_combat.util;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record SoundOptions(Supplier<SoundEvent> sound, float volume, float pitch, float pitchVariance) {

    public void playSound(final Level level, final @Nullable Player player,
            final double x, final double y, final double z,
            final SoundSource source) {
        level.playSound(player, x, y, z, this.sound().get(), source,
                this.volume(), (float) level.getRandom().triangle(this.pitch(), this.pitchVariance()));
    }
}
