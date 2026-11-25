package dev.nekotune.critical_combat.client.model;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.model.effects.FirstPersonSwingAnimations;
import dev.nekotune.critical_combat.client.model.effects.FirstPersonSwingAnimations.FirstPersonSwingAnimation;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.mixin.minecraft.client.ItemInHandRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;

/**
 * Extends the functionality of the ItemInHandRenderer for
 * {@link CriticalCombat}
 * 
 * @see ItemInHandRendererMixin
 */
public final class CCItemInHandRenderer_ {

    /**
     * Returns the extension associated with the given ItemInHandRenderer.
     * 
     * @param renderer The ItemInHandRenderer to get the extension for
     * @return CCItemInHandRenderer extension for the given renderer
     */
    @SuppressWarnings("unchecked")
    public static CCItemInHandRenderer_ get(final ItemInHandRenderer renderer) {
        return ((MixinExtension<CCItemInHandRenderer_>) renderer).critical_combat$getExtension();
    }

    public final ItemInHandRenderer itemInHandRenderer;
    public final Minecraft minecraft;
    public final EntityRenderDispatcher entityRenderDispatcher;
    public final ItemRenderer itemRenderer;

    /**
     * @see ItemInHandRendererMixin#critical_combat$initItemInHandRendererExtension
     */
    public CCItemInHandRenderer_(final ItemInHandRenderer itemInHandRenderer,
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
            final HumanoidArm arm, final float swingProgress, final float partialTicks,
            final CallbackInfo ci) {
        final @Nullable LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            final InteractionHand hand = arm == player.getMainArm()
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
            final Item item = player.getItemInHand(hand).getItem();
            final @Nullable FirstPersonSwingAnimation swingAnimation = FirstPersonSwingAnimations.get(item);
            if (swingAnimation != null) {

                // Undo default hardcoded translation for swinging
                final int side = arm == HumanoidArm.RIGHT ? 1 : -1;
                final float x = -0.4F * Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
                final float y = 0.2F * Mth.sin(Mth.sqrt(swingProgress) * (float) (Math.PI * 2));
                final float z = -0.2F * Mth.sin(swingProgress * (float) Math.PI);
                poseStack.translate((float) side * -x, -y, -z);
                
                // Apply custom swinging animation
                swingAnimation.applyTransform(poseStack, arm, swingProgress, partialTicks);

                ci.cancel();
            }
        }
    }
}
