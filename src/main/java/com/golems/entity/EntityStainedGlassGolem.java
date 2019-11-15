package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityStainedGlassGolem extends GolemColorizedMultiTextured {

	public static final String PREFIX = "stained_glass";
	public static final int[] COLOR_ARRAY = ItemDye.DYE_COLORS;

	private static final ResourceLocation TEXTURE_BASE = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM);
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
		.makeTexture(ExtraGolems.MODID, GolemNames.STAINEDGLASS_GOLEM + "_grayscale");

	public EntityStainedGlassGolem(final World world) {
		super(world, TEXTURE_BASE, TEXTURE_OVERLAY, COLOR_ARRAY);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc(GolemNames.STAINEDGLASS_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	/**
	 * Whether {@link overlay} should be rendered as transparent. Is not called for rendering
	 * {@link base}
	 **/
	@Override
	public boolean hasTransparency() {
		return true;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture (defaults to last item of color array)
		final int meta = body.getBlock().getMetaFromState(body)
			% this.getColorArray().length;
		this.setTextureNum((byte) (this.getColorArray().length - meta - 1));
	}
}
