package dev.nekotune.critical_combat.mixin.minecraft.shared;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.minecraft.world.item.DiggerItem;

@Mixin(value = DiggerItem.class)
public abstract class DiggerItemMixin {

    /**
     * Remove the extra point of durability lost when attacking with tools.
     */
    @ModifyConstant(method = "postHurtEnemy", constant = @Constant(intValue = 2))
    private int critical_combat$removeExtraToolCombatWear(final int original) {
        return 1;
    }
}
