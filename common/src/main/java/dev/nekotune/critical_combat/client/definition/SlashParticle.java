package dev.nekotune.critical_combat.client.definition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Particles for weapon slashes.
 */
@ParametersAreNonnullByDefault
public class SlashParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final SlashParticleOptions options;
    private final @Nullable Entity followEntity;
    private final double offsetX, offsetY, offsetZ;

    protected SlashParticle(final ClientLevel level, final double x, final double y, final double z,
            final SlashParticleOptions options, final float size, final int lifetime,
            final SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.options = options;
        final @Nullable Entity followEntity = options.followEntityId() != SlashParticleOptions.NO_ENTITY
                ? level.getEntity(options.followEntityId())
                : null;
        this.followEntity = followEntity;
        if (followEntity != null) {
            this.offsetX = x - followEntity.getX();
            this.offsetY = y - followEntity.getY();
            this.offsetZ = z - followEntity.getZ();
        } else {
            this.offsetX = this.offsetY = this.offsetZ = 0D;
        }
        this.oRoll = options.roll() * Mth.DEG_TO_RAD;
        this.roll = this.oRoll;
        this.lifetime = lifetime;
        this.quadSize = size * this.options.scale();
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    private float yaw() {
        return (float) Math.PI - this.options.yaw() * Mth.DEG_TO_RAD;
    }

    private float pitch() {
        return -this.options.pitch() * Mth.DEG_TO_RAD;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (quaternion, camera, partialTicks) -> quaternion.rotationYXZ(this.yaw(), this.pitch(), 0f);
    }

    @Override
    public void render(final VertexConsumer buffer, final Camera camera,
            final float partialTicks) {
        super.render(buffer, camera, partialTicks);
        final Quaternionf back = new Quaternionf();
        this.getFacingCameraMode().setRotation(back, camera, partialTicks);
        back.rotateY((float) Math.PI).rotateZ(-Mth.lerp(partialTicks, this.oRoll, this.roll));
        this.renderRotatedQuad(buffer, camera, back, partialTicks);
    }

    @Override
    public void tick() {
        final @Nullable Entity followEntity = this.followEntity;
        if (followEntity != null && !followEntity.isRemoved()) {
            this.xo = followEntity.xOld + this.offsetX;
            this.yo = followEntity.yOld + this.offsetY;
            this.zo = followEntity.zOld + this.offsetZ;
            this.setPos(followEntity.getX() + this.offsetX,
                    followEntity.getY() + this.offsetY,
                    followEntity.getZ() + this.offsetZ);
        } else {
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
        }
        if (this.age++ >= this.lifetime)
            this.remove();
        else
            this.setSpriteFromAge(this.sprites);
    }

    @Override
    protected float getU0() {
        return this.options.flipped() ? super.getU1() : super.getU0();
    }

    @Override
    protected float getU1() {
        return this.options.flipped() ? super.getU0() : super.getU1();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites, float size, int lifetime)
            implements ParticleProvider<SlashParticleOptions> {
        @Override
        public Particle createParticle(final SlashParticleOptions options, final ClientLevel level,
                final double x, final double y, final double z,
                final double xSpeed, final double ySpeed, final double zSpeed) {
            return new SlashParticle(level, x, y, z, options, this.size, this.lifetime, this.sprites);
        }
    }
}
