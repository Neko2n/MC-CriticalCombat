package dev.nekotune.critical_combat.mixin.minecraft.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.nekotune.critical_combat.client.model.CCItemInHandRenderer_;
import dev.nekotune.critical_combat.mixin.MixinExtension;
import dev.nekotune.critical_combat.shared.CCPlayer_;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

@Mixin(value = ItemInHandRenderer.class)
public class ItemInHandRendererMixin implements MixinExtension<CCItemInHandRenderer_> {

    @Unique
    private CCItemInHandRenderer_ critical_combat$itemInHandRendererExtension;

    @Override
    public CCItemInHandRenderer_ critical_combat$getExtension() {
        return this.critical_combat$itemInHandRendererExtension;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void critical_combat$initItemInHandRendererExtension(final Minecraft minecraft,
            final EntityRenderDispatcher entityRenderDispatcher, final ItemRenderer itemRenderer,
            final CallbackInfo ci) {
        final ItemInHandRenderer itemInHandRenderer = (ItemInHandRenderer) (Object) this;
        this.critical_combat$itemInHandRendererExtension = new CCItemInHandRenderer_(
                itemInHandRenderer, minecraft, entityRenderDispatcher, itemRenderer);
    }

    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true)
    private void critical_combat$hookApplyItemArmAttackTransform(final PoseStack poseStack,
            final HumanoidArm arm, final float swingProgress, final CallbackInfo ci) {
        final float partialTicks = Minecraft.getInstance().getTimer()
                .getGameTimeDeltaPartialTick(true);
        this.critical_combat$itemInHandRendererExtension
                .applyItemArmAttackTransform(poseStack, arm, swingProgress, partialTicks, ci);
    }

    /**
     * Backported fix from 1.21.11 where the swap animation only plays when you
     * actually swap items.
     */
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private float critical_combat$useItemSwapScale(final LocalPlayer player, final float partialTick,
            final Operation<Float> original) {
        final CCPlayer_ ccplayer = CCPlayer_.get(player);
        return Mth.clamp((ccplayer.swapTicks + partialTick) / player.getCurrentItemAttackStrengthDelay(), 0f, 1f);
    }

    /**
     * Backported fix from 1.21.11 stopping comparisons from failing when
     * an item loses durability.
     * (fixes the re-equip animation playing when an item takes damage)
     */
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean critical_combat$ignoreDamageForSwap(final ItemStack a, final ItemStack b,
            final Operation<Boolean> original) {
        if (original.call(a, b))
            return true;
        if (!ItemStack.isSameItem(a, b))
            return false;
        final ItemStack a2 = a.copy(), b2 = b.copy();
        a2.remove(DataComponents.DAMAGE);
        b2.remove(DataComponents.DAMAGE);
        return ItemStack.matches(a2, b2);
    }
}
