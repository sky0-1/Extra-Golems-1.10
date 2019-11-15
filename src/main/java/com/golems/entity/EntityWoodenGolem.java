package com.golems.entity;

import com.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityWoodenGolem extends GolemMultiTextured {

	public static final String WOOD_PREFIX = "wooden";
	public static final String[] woodTypes = {"oak", "spruce", "birch", "jungle", "acacia",
		"big_oak"};
	
	public EntityWoodenGolem(final World world) {
		super(world, WOOD_PREFIX, woodTypes);
		this.setCanSwim(true);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	@Override
	public ItemStack getCreativeReturn() {
		// try to return the same block of this golem's texture
		Block block = Blocks.LOG;
		int damage = this.getTextureNum() % woodTypes.length;
		if (this.getTextureNum() > 3) {
			block = Blocks.LOG2;
			damage %= 2;
		}
		return new ItemStack(block, 1, damage);
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block metadata to give this golem the right texture
		if(body.getBlock() instanceof BlockNewLog || body.getBlock() instanceof BlockOldLog) {
			final int meta = body.getBlock().getMetaFromState(
			body.withProperty(BlockLog.LOG_AXIS, EnumAxis.NONE));
			byte textureNum = body.getBlock() == Blocks.LOG2 ? (byte) (meta + 4) : (byte) meta;
			textureNum %= this.getNumTextures();
			this.setTextureNum(textureNum);
		}
		
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
}
