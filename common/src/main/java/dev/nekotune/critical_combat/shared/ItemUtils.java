package dev.nekotune.critical_combat.shared;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface ItemUtils {
    
    public static double getAttribute(final Attribute attribute, final Item item) {
        final ItemAttributeModifiers modifiers = item.components()
                .getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        final double[] add = { attribute.getDefaultValue() }; // 4.0
        final double[] multiplyBase = { 0.0 };
        final double[] multiplyTotal = { 1.0 };
        modifiers.forEach(EquipmentSlot.MAINHAND, (other, modifier) -> {
            if (other.value() != attribute)
                return;
            switch (modifier.operation()) {
                case ADD_VALUE -> add[0] += modifier.amount();
                case ADD_MULTIPLIED_BASE -> multiplyBase[0] += modifier.amount();
                case ADD_MULTIPLIED_TOTAL -> multiplyTotal[0] *= 1.0 + modifier.amount();
            }
        });
        return attribute.sanitizeValue((add[0] + add[0] * multiplyBase[0]) * multiplyTotal[0]);
    }
}
