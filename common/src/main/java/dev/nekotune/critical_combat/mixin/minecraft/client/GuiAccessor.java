package dev.nekotune.critical_combat.mixin.minecraft.client;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.phys.HitResult;

@Mixin(value = Gui.class)
public interface GuiAccessor {
    
    @Invoker(value = "canRenderCrosshairForSpectator")
    public boolean critical_combat$invokeCanRenderCrosshairForSpectator(@Nullable final HitResult rayTrace);

    @Accessor(value = "minecraft")
    public Minecraft critical_combat$getMinecraft();
}
