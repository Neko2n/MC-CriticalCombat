package dev.nekotune.critical_combat.shared.definition;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * LIGHT-type weapon that can be thrown as a short-range projectile.
 */
@ParametersAreNonnullByDefault
public class DaggerItem extends Item implements ProjectileItem {

	public DaggerItem(final Properties properties) {
		super(properties.component(DataComponents.TOOL, createToolProperties()));
	}

	/** Copied from {@link SwordItem#createToolProperties} */
	private static Tool createToolProperties() {
		return new Tool(List.of(Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F),
				Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
	}

	public static ItemAttributeModifiers createAttributes(final float attackDamage, final float attackSpeed) {
		return ItemAttributeModifiers.builder()
				.add(
						Attributes.ATTACK_DAMAGE,
						new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double) attackDamage,
								AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.add(
						Attributes.ATTACK_SPEED,
						new AttributeModifier(BASE_ATTACK_SPEED_ID, (double) attackSpeed,
								AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.build();
	}

	@Override
	public boolean canAttackBlock(final BlockState state, final Level level, final BlockPos pos, final Player player) {
		return !player.isCreative();
	}

	@Override
	public boolean hurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
		return true;
	}

	@Override
	public void postHurtEnemy(final ItemStack stack, final LivingEntity target, final LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

	@Override
	public Projectile asProjectile(final Level level, final Position pos, final ItemStack stack, final Direction direction) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'asProjectile'");
	}
}
