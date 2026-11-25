package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.nekotune.critical_combat.client.CC$ItemInHandRenderer_;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.HumanoidArm;

@Mixin(value = ItemInHandRenderer.class)
public class ItemInHandRendererMixin implements MixinExtension<CC$ItemInHandRenderer_> {

    @Unique
    private CC$ItemInHandRenderer_ critical_combat$itemInHandRendererExtension;

    @Override
    public CC$ItemInHandRenderer_ critical_combat$getExtension() {
        return this.critical_combat$itemInHandRendererExtension;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void critical_combat$initItemInHandRendererExtension(final Minecraft minecraft,
            final EntityRenderDispatcher entityRenderDispatcher, final ItemRenderer itemRenderer,
            final CallbackInfo ci) {
        final ItemInHandRenderer itemInHandRenderer = (ItemInHandRenderer) (Object) this;
        this.critical_combat$itemInHandRendererExtension = new CC$ItemInHandRenderer_(
                itemInHandRenderer, minecraft, entityRenderDispatcher, itemRenderer);
    }

    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true)
    private void critical_combat$hookApplyItemArmAttackTransform(final PoseStack poseStack,
            final HumanoidArm arm, final float swingProgress, final CallbackInfo ci) {
        this.critical_combat$itemInHandRendererExtension.applyItemArmAttackTransform(poseStack, arm, swingProgress, ci);
    }
}
