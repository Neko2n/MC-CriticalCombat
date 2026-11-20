package dev.nekotune.critical_combat.client.gui;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.joml.Matrix4fStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.mixin.minecraft.client.GuiAccessor;
import dev.nekotune.critical_combat.mixin.minecraft.client.GuiMixin;
import dev.nekotune.critical_combat.shared.CCPlayerExtension;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.SharedConstants;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;

/**
 * Custom crosshair override for {@link CriticalCombat}
 * 
 * @see GuiMixin#critical_combat$replaceCrosshair
 */
public class CCCrosshair {

    protected final Minecraft minecraft;
    protected final Gui gui;
    protected float flashIntensity = 0f;

    /**
     * @see GuiMixin#critical_combat$initCrosshair
     */
    public CCCrosshair(final Minecraft minecraft, final Gui gui) {
        this.minecraft = minecraft;
        this.gui = gui;
    }

    /**
     * @return The crosshair attached to the given Gui
     */
    public static CCCrosshair get(final Gui gui) {
        return ((CCCrosshair.MixinHolder) gui).critical_combat$getCrosshair();
    }

    /**
     * @return The crosshair's sprites
     */
    protected CrosshairSprites sprites() {
        return CrosshairSprites.DEFAULT;
    }

    /**
     * Flashes the attack indicator.
     * Used to give attack feedback for LIGHT-type weapons.
     * Only visible while holding out a LIGHT-type weapon.
     */
    public void flashAttackIndicator() {
        flashIntensity = 1f;
    }

    /**
     * Renders the crosshair to the GUI.
     * Derived from {@link Gui#renderCrosshair}
     * 
     * @param guiGraphics  The GUI graphics instance to render to
     * @param deltaTracker DeltaTracker
     */
    public final void render(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker) {
        final LocalPlayer localPlayer = Objects.requireNonNull(this.minecraft.player);
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if (Objects.requireNonNull(this.minecraft.gameMode).getPlayerMode() != GameType.SPECTATOR
                    || ((GuiAccessor) this.gui)
                            .critical_combat$invokeCanRenderCrosshairForSpectator(this.minecraft.hitResult)) {
                RenderSystem.enableBlend();
                if (this.gui.getDebugOverlay().showDebugScreen()
                        && !localPlayer.isReducedDebugInfo()
                        && !this.minecraft.options.reducedDebugInfo().get()) {
                    final Camera camera = this.minecraft.gameRenderer.getMainCamera();
                    final Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
                    matrix4fStack.pushMatrix();
                    matrix4fStack.mul(guiGraphics.pose().last().pose());
                    matrix4fStack.translate((float) (guiGraphics.guiWidth() / 2), (float) (guiGraphics.guiHeight() / 2),
                            0.0F);
                    matrix4fStack.rotateX(-camera.getXRot() * (float) (Math.PI / 180.0));
                    matrix4fStack.rotateY(camera.getYRot() * (float) (Math.PI / 180.0));
                    matrix4fStack.scale(-1.0F, -1.0F, -1.0F);
                    RenderSystem.applyModelViewMatrix();
                    RenderSystem.renderCrosshair(10);
                    matrix4fStack.popMatrix();
                    RenderSystem.applyModelViewMatrix();
                } else {
                    renderCrosshair(guiGraphics, deltaTracker);
                    if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                        renderAttackIndicator(guiGraphics, deltaTracker);
                    }
                }
                RenderSystem.disableBlend();
            }
        }
    }

    /**
     * Renders the attack indicator to the GUI.
     * Derived from {@link Gui#renderCrosshair}
     * 
     * @param guiGraphics  The GUI graphics instance to render to
     * @param deltaTracker DeltaTracker
     */
    protected void renderCrosshair(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker) {
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        guiGraphics.blitSprite(this.sprites().crosshair(), (guiGraphics.guiWidth() - 15) / 2,
                (guiGraphics.guiHeight() - 15) / 2, 15, 15);
        RenderSystem.defaultBlendFunc();
    }

    /**
     * Renders the crosshair's attack indicator to the GUI.
     * Derived from {@link Gui#renderCrosshair}
     * 
     * @param guiGraphics  The GUI graphics instance to render to
     * @param deltaTracker DeltaTracker
     */
    protected void renderAttackIndicator(final GuiGraphics guiGraphics,
            final DeltaTracker deltaTracker) {

        // Inverted colors blend function, matching the crosshair
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        final float deltaTime = deltaTracker.getRealtimeDeltaTicks() / SharedConstants.TICKS_PER_SECOND;
        final LocalPlayer localPlayer = Objects.requireNonNull(this.minecraft.player);
        final CCPlayerExtension ext = CCPlayerExtension.get(localPlayer);
        final float attackStrengthScale = localPlayer.getAttackStrengthScale(0.0F);
        final @Nullable WeaponType heldWeaponType = WeaponTypes.get(localPlayer.getWeaponItem().getItem());
        final boolean holdingLightWeapon = heldWeaponType == WeaponType.LIGHT;

        // LIGHT-type flash feedback
        if (holdingLightWeapon) {
            flashIntensity = Math.max(0f, flashIntensity - deltaTime * 4f);
        } else {
            flashIntensity = 0f;
        }

        // Calculate whether the indicator should render the "+" symbol
        boolean hasPickedEntity = false;
        if (this.minecraft.crosshairPickEntity != null
                && this.minecraft.crosshairPickEntity instanceof LivingEntity && attackStrengthScale >= 1.0F) {
            hasPickedEntity = localPlayer.getCurrentItemAttackStrengthDelay() > 5.0F
                    || holdingLightWeapon;
            hasPickedEntity &= Objects.requireNonNull(this.minecraft.crosshairPickEntity).isAlive();
        }

        // Actually render the attack indicator
        final int x = guiGraphics.guiWidth() / 2 - 8;
        final int y = guiGraphics.guiHeight() / 2 - 7 + 16;
        final float fillAmount = (attackStrengthScale < 1f) ? attackStrengthScale : flashIntensity;
        final int l = (int) (fillAmount * 17f);
        final Consumer<Boolean> blitAttackIndicator = renderThePlus -> {

            // Blit the "+" part of the indicator
            if (renderThePlus)
                guiGraphics.blitSprite(this.sprites().attackIndicator$plus(), x, y, 16, 16);

            // Blit the unfilled indicator background
            guiGraphics.blitSprite(this.sprites().attackIndicator$background(), x, y, 16, 4);

            // Blit the indicator's fill/progress
            guiGraphics.blitSprite(this.sprites().attackIndicator$progress(), 16, 4, 0, 0, x, y, l, 4);
        };
        final boolean isRendered = hasPickedEntity || attackStrengthScale < 1f
                || (holdingLightWeapon && flashIntensity > 0f);
        if (isRendered) {
            blitAttackIndicator.accept(hasPickedEntity);
        }

        // Apply a color filter to the indicator as the player's heat increases
        if (isRendered && holdingLightWeapon) {
            RenderSystem.defaultBlendFunc();
            guiGraphics.setColor(1f, 0.2f, 0.2f, ext.getHeat() * 0.5f);
            blitAttackIndicator.accept(false);
        }

        // Reset render state
        guiGraphics.setColor(1f, 1f, 1f, 1f);
        RenderSystem.defaultBlendFunc();
    }

    public static record CrosshairSprites(ResourceLocation crosshair,
            ResourceLocation attackIndicator$plus,
            ResourceLocation attackIndicator$background,
            ResourceLocation attackIndicator$progress) {

        /** Sprites derived from {@link Gui} */
        public static final CrosshairSprites DEFAULT = new CrosshairSprites(
                ResourceLocation.withDefaultNamespace("hud/crosshair"),
                ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, "hud/crosshair_attack_indicator_plus"),
                ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background"),
                ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress"));
    }

    /**
     * @see GuiMixin
     */
    @FunctionalInterface
    public static interface MixinHolder {
        public CCCrosshair critical_combat$getCrosshair();
    }
}
