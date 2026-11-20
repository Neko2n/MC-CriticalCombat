package dev.nekotune.critical_combat.client.gui;

import java.util.Objects;

import org.joml.Matrix4fStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.nekotune.critical_combat.mixin.minecraft.client.GuiAccessor;
import dev.nekotune.critical_combat.mixin.minecraft.client.GuiMixin;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
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

public class CCCrosshair {

    /** @see Gui#CROSSHAIR_SPRITE */
    public static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");

    /** @see Gui#CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE */
    public static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation
            .withDefaultNamespace("hud/crosshair_attack_indicator_full");

    /** @see Gui#CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE */
    public static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation
            .withDefaultNamespace(
                    "hud/crosshair_attack_indicator_background");

    /** @see Gui#CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE */
    public static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation
            .withDefaultNamespace(
                    "hud/crosshair_attack_indicator_progress");

    /**
     * Renders the crosshair to the GUI.
     * Derived from {@link Gui#renderCrosshair}
     * 
     * @param guiGraphics  The GUI graphics instance to render to
     * @param deltaTracker DeltaTracker
     * @see GuiMixin#critical_combat$replaceCrosshair
     */
    public static void render(final Gui gui, final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = ((GuiAccessor) gui).critical_combat$getMinecraft();
        final LocalPlayer localPlayer = Objects.requireNonNull(minecraft.player);
        if (minecraft.options.getCameraType().isFirstPerson()) {
            if (Objects.requireNonNull(minecraft.gameMode).getPlayerMode() != GameType.SPECTATOR
                    || ((GuiAccessor) gui).critical_combat$invokeCanRenderCrosshairForSpectator(minecraft.hitResult)) {
                RenderSystem.enableBlend();
                if (gui.getDebugOverlay().showDebugScreen()
                        && !localPlayer.isReducedDebugInfo()
                        && !minecraft.options.reducedDebugInfo().get()) {
                    final Camera camera = minecraft.gameRenderer.getMainCamera();
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
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                            GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO);
                    guiGraphics.blitSprite(CROSSHAIR_SPRITE, (guiGraphics.guiWidth() - 15) / 2,
                            (guiGraphics.guiHeight() - 15) / 2, 15, 15);
                    if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                        final float f = localPlayer.getAttackStrengthScale(0.0F);
                        boolean bl = false;
                        if (minecraft.crosshairPickEntity != null
                                && minecraft.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
                            bl = localPlayer.getCurrentItemAttackStrengthDelay() > 5.0F
                                    // Still renders the crosshair properly for light weapons, ignoring attack strength delay
                                    || WeaponTypes.get(localPlayer.getWeaponItem().getItem()) == WeaponType.LIGHT;
                            bl &= Objects.requireNonNull(minecraft.crosshairPickEntity).isAlive();
                        }

                        final int j = guiGraphics.guiHeight() / 2 - 7 + 16;
                        final int k = guiGraphics.guiWidth() / 2 - 8;
                        if (bl) {
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
                        } else if (f < 1.0F) {
                            final int l = (int) (f * 17.0F);
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
                            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
                        }
                    }

                    RenderSystem.defaultBlendFunc();
                }

                RenderSystem.disableBlend();
            }
        }
    }
}
