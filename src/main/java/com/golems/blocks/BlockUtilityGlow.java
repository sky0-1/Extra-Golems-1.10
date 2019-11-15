package com.golems.blocks;

import java.util.List;
import java.util.Random;

import com.golems.entity.GolemBase;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtilityGlow extends BlockUtility {
	public static final PropertyInteger LIGHT_LEVEL = PropertyInteger.create("light", 0, 15);
	private final IBlockState REPLACE_WITH;
	private final int TICK_RATE;

	public BlockUtilityGlow(Material m, final float defaultLight, final int tickRate, final IBlockState replaceWith) {
		super(m);
		int light = (int) (defaultLight * 15.0F);
		this.setTickRandomly(true);
		this.setLightLevel(defaultLight);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LIGHT_LEVEL, light));
		this.TICK_RATE = tickRate;
		this.REPLACE_WITH = replaceWith;
	}

	@Override
	public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
		// make a slightly expanded AABB to check for the golem
		AxisAlignedBB toCheck = new AxisAlignedBB(pos).expandXyz(0.5D);
		List<GolemBase> list = worldIn.getEntitiesWithinAABB(GolemBase.class, toCheck);
		boolean hasLightGolem = list != null && !list.isEmpty() && hasLightGolem(list);

		if (!hasLightGolem) {
			// remove this block
			worldIn.setBlockState(pos, REPLACE_WITH, 3);
		} else {
			// schedule another update
			worldIn.scheduleUpdate(pos, this, TICK_RATE);
		}
	}

	/**
	 * Called after the block is set in the Chunk data, but before the Tile Entity is set
	 */
	@Override
	public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}

	@Override
	public int tickRate(final World worldIn) {
		return TICK_RATE;
	}

	@Override
	public boolean requiresUpdates() {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{BlockUtilityGlow.LIGHT_LEVEL});
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 **/
	@Override
	public IBlockState getStateFromMeta(final int metaIn) {
		int meta = metaIn;
		if (meta < 0)
			meta = 0;
		if (meta > 15)
			meta = 15;
		return this.getDefaultState().withProperty(LIGHT_LEVEL, meta);
	}
	
	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(LIGHT_LEVEL);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 **/
	@Override
	public int getMetaFromState(final IBlockState state) {
		return state.getValue(LIGHT_LEVEL).intValue();
	}

	protected boolean hasLightGolem(List<GolemBase> list) {
		for(final GolemBase golem : list) {
			if(golem.isProvidingLight()) {
				return true;
			}
		}
		return false;
	}
}
