package dev.nekotune.critical_combat.client;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.mixin.minecraft.client.ItemInHandRendererMixin;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Extends the functionality of the ItemInHandRenderer for
 * {@link CriticalCombat}
 * 
 * @see ItemInHandRendererMixin
 */
public final class CC$ItemInHandRenderer_ {

    /**
     * Returns the extension associated with the given ItemInHandRenderer.
     * 
     * @param renderer The ItemInHandRenderer to get the extension for
     * @return CC$ItemInHandRenderer extension for the given renderer
     */
    @SuppressWarnings("unchecked")
    public static CC$ItemInHandRenderer_ get(final ItemInHandRenderer renderer) {
        return ((MixinExtension<CC$ItemInHandRenderer_>) renderer).critical_combat$getExtension();
    }

    public final ItemInHandRenderer itemInHandRenderer;
    public final Minecraft minecraft;
    public final EntityRenderDispatcher entityRenderDispatcher;
    public final ItemRenderer itemRenderer;

    /**
     * @see ItemInHandRendererMixin#critical_combat$initItemInHandRendererExtension
     */
    public CC$ItemInHandRenderer_(final ItemInHandRenderer itemInHandRenderer,
            final Minecraft minecraft, final EntityRenderDispatcher entityRenderDispatcher,
            final ItemRenderer itemRenderer) {
        this.itemInHandRenderer = itemInHandRenderer;
        this.minecraft = minecraft;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.itemRenderer = itemRenderer;
    }

    /**
     * @see ItemInHandRendererMixin#critical_combat$hookApplyItemArmAttackTransform
     */
    public final void applyItemArmAttackTransform(final PoseStack poseStack,
            final HumanoidArm arm, final float swingProgress, final CallbackInfo ci) {
        final @Nullable LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {

            // Custom first-person swinging animations for different weapon types.
            final InteractionHand hand = arm == player.getMainArm()
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
            final @Nullable WeaponType heldWeaponType = WeaponTypes
                    .get(player.getItemInHand(hand).getItem());
            if (heldWeaponType != null) {
                final ApplyAttackTransform transform = switch (heldWeaponType) {
                    case BLADE -> this::applyBladeAttackTransform;
                    case HEAVY -> this::applyHeavyAttackTransform;
                    case POLEARM -> this::applyPolearmAttackTransform;
                    default -> this::applyDefaultAttackTransform;
                };
                transform.accept(poseStack, arm, swingProgress);
                ci.cancel(); // Override vanilla animation
                return;
            }
        }
    }

    protected void applyDefaultAttackTransform(final PoseStack poseStack, final HumanoidArm arm,
            final float swingProgress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyBladeAttackTransform'");
    }

    protected void applyBladeAttackTransform(final PoseStack poseStack, final HumanoidArm arm,
            final float swingProgress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyBladeAttackTransform'");
    }

    protected void applyHeavyAttackTransform(final PoseStack poseStack, final HumanoidArm arm,
            final float swingProgress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyBladeAttackTransform'");
    }

    protected void applyPolearmAttackTransform(final PoseStack poseStack, final HumanoidArm arm,
            final float swingProgress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyBladeAttackTransform'");
    }

    @FunctionalInterface
    private static interface ApplyAttackTransform {
        public void accept(final PoseStack poseStack, final HumanoidArm arm,
                final float swingProgress);
    }
}
