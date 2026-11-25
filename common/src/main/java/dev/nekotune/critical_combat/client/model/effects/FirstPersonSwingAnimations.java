package dev.nekotune.critical_combat.client.model.effects;

import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import dev.nekotune.critical_combat.shared.registry.tags.CCItemTags;
import dev.nekotune.critical_combat.util.Ease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.ItemLike;

public interface FirstPersonSwingAnimations {

    /**
     * Returns the swing animation associated with the given item.
     * 
     * @param item The item to look up the swing animation for
     * @return SwingAnimation, or null if no swing animation is associated with the
     *         given item
     */
    public static @Nullable FirstPersonSwingAnimation get(final ItemLike item) {
        final @Nullable WeaponType heldWeaponType = WeaponTypes.get(item);
        if (heldWeaponType != null) {
            return switch (heldWeaponType) {
                case BLADE, LIGHT -> FirstPersonSwingAnimations.SLASH;
                case POLEARM -> FirstPersonSwingAnimations.STAB;
                default -> FirstPersonSwingAnimations.WHACK;
            };
        }
        if (item.asItem().getDefaultInstance().is(CCItemTags.UPWARDS_KNOCKBACK))
            return FirstPersonSwingAnimations.SCOOP;
        return null;
    }

    /** Derived from vanilla's default swinging animation */
    public static FirstPersonSwingAnimation WHACK = (final PoseStack pose, final HumanoidArm arm,
            final float swingProgress, final float partialTicks) -> {
        final float windup = (float) Ease.inOutSine(keyframe(swingProgress, 0f, 0.05f));
        final float recover = (float) Ease.inOutSine(keyframe(swingProgress, 0.05f, 1.0f));
        final int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        
        // Attack cooldown feedback
        final LocalPlayer localPlayer = Objects.requireNonNull(Minecraft.getInstance().player);
        final float f = localPlayer.getAttackStrengthScale(partialTicks);
        final float handHeight = -0.4f + Math.min(f * f * f, 0.4f);
        pose.translate(0f, handHeight - handHeight * (windup - recover), 0f);
        
        // Whacking animation
        final float x = -0.4f * Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
        final float y = 0.2f * Mth.sin(Mth.sqrt(swingProgress) * (float) (Math.PI * 2));
        final float z = -0.2f * Mth.sin(swingProgress * (float) Math.PI);
        pose.translate((float) side * x, y, z);
        final float swingDistExp = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
        final float swingDistSqr = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
        pose.mulPose(Axis.YP.rotationDegrees((float) side * (45f + swingDistExp * -20f)));
        pose.mulPose(Axis.ZP.rotationDegrees((float) side * swingDistSqr * -20f));
        pose.mulPose(Axis.XP.rotationDegrees(swingDistSqr * -80f));
        pose.mulPose(Axis.YP.rotationDegrees((float) side * -45f));
    };

    // Version of whack that swings more sideways along the "blade" of the weapon
    public static FirstPersonSwingAnimation SLASH = (final PoseStack pose, final HumanoidArm arm,
            final float swingProgress, final float partialTicks) -> {
        final float windup = (float) Ease.inOutSine(keyframe(swingProgress, 0f, 0.05f));
        final float thrust = (float) Ease.outBack(keyframe(swingProgress, 0.05f, 0.2f));
        final float swing = (float) Ease.outBack(keyframe(swingProgress, 0.05f, 0.6f));
        final float recover = (float) Ease.inOutExpo(keyframe(swingProgress, 0.4f, 1.0f));
        final int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        
        // Attack cooldown feedback
        final LocalPlayer localPlayer = Objects.requireNonNull(Minecraft.getInstance().player);
        final float f = localPlayer.getAttackStrengthScale(partialTicks);
        final float handHeight = -0.4f + Math.min(f * f * f, 0.4f);
        pose.translate(0f, handHeight - handHeight * (windup - recover), 0f);

        // Slashing animation
        // pose.translate(sqr * -0.5f, 0f, 0f);
        pose.translate(0f, 0.3f * (windup - thrust), 0.1f * (windup - swing));
        pose.mulPose(Axis.XP.rotationDegrees((float) side * (windup - swing) * 20f));
        pose.mulPose(Axis.YP.rotationDegrees((float) side * (windup - swing) * 10f));
        pose.translate(side * -0.5f * (swing - recover), 0.1f * (thrust - recover), -0.2f * (swing - recover));
        pose.mulPose(Axis.ZP.rotationDegrees((float) side * (thrust - recover) * -10f));
        pose.mulPose(Axis.XP.rotationDegrees((float) side * (thrust - recover) * -90f));
        pose.mulPose(Axis.YP.rotationDegrees((float) side * (thrust - recover) * 10f));
        // WHACK.applyTransform(pose, arm, swingProgress);
        // pose.mulPose(Axis.YP.rotationDegrees((float) side * thrust * 40f));
        // pose.mulPose(Axis.ZP.rotationDegrees((float) side * windup * -70f));
        // pose.mulPose(Axis.XP.rotationDegrees((float) side * sqr * -120f));
    };

    /** Copied from vanilla's spear stab animation in 1.21.11+ */
    public static FirstPersonSwingAnimation STAB = (final PoseStack pose, final HumanoidArm arm,
            final float swingProgress, final float partialTicks) -> {
        final float windup = (float) Ease.inOutSine(keyframe(swingProgress, 0f, 0.05f));
        final float thrust = (float) Ease.outBack(keyframe(swingProgress, 0.05f, 0.2f));
        final float recover = (float) Ease.inOutExpo(keyframe(swingProgress, 0.4f, 1.0f));
        final int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        
        // Attack cooldown feedback
        final LocalPlayer localPlayer = Objects.requireNonNull(Minecraft.getInstance().player);
        final float f = localPlayer.getAttackStrengthScale(partialTicks);
        final float handHeight = -0.4f + Math.min(f * f * f, 0.4f);
        pose.translate(0f, handHeight - handHeight * (windup - recover), 0f);
        
        // Stabbing animation
        pose.translate(side * 0.1f * (windup - thrust), -0.075f * (windup - recover), 0.65f * (windup - thrust));
        pose.mulPose(Axis.XP.rotationDegrees(-70.0f * (windup - recover)));
        pose.translate(0f, 0f, 0.25f * (windup - recover));
    };

    // Whack, but mostly inverted
    public static FirstPersonSwingAnimation SCOOP = (final PoseStack pose, final HumanoidArm arm,
            final float swingProgress, final float partialTicks) -> {
        WHACK.applyTransform(pose, arm, swingProgress, partialTicks);
    };

    private static float keyframe(final float t, final float start, final float end) {
        return Mth.clamp(Mth.inverseLerp(t, start, end), 0f, 1f);
    }

    @FunctionalInterface
    public static interface FirstPersonSwingAnimation {
        public void applyTransform(final PoseStack pose, final HumanoidArm arm,
                final float swingProgress, final float partialTicks);
    }
}
