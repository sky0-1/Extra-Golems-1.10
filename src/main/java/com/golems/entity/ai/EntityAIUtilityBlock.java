package com.golems.entity.ai;

import java.util.function.BiPredicate;

import com.golems.entity.GolemBase;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Places a single IBlockState every {@code tickDelay} ticks with certain conditions
 **/
public class EntityAIUtilityBlock extends EntityAIBase {

	public final GolemBase golem;
	public final IBlockState stateToPlace;
	public final int tickDelay;
	public final boolean configAllows;
	public final BiPredicate<GolemBase, IBlockState> predicate;

	/**
	 * @param golemIn        the GolemBase to use
	 * @param stateIn        the IBlockState that will be placed every {@code interval} ticks
	 * @param interval       ticks between placing block
	 * @param cfgAllows		 whether this AI is enabled by the config
	 * @param canReplacePred an optional BiPredicate to use when determining whether to place a Block.
	 * Defaults to replacing air only.
	 * @see #getDefaultBiPred(GolemBase, IBlockState)
	 **/
	public EntityAIUtilityBlock(final GolemBase golemIn, final IBlockState stateIn, final int interval, 
			final boolean cfgAllows, final BiPredicate<GolemBase, IBlockState> canReplacePred) {
		this.setMutexBits(8);
		this.golem = golemIn;
		this.stateToPlace = stateIn;
		this.tickDelay = interval;
		this.configAllows = cfgAllows;
		this.predicate = canReplacePred;
	}

	/**
	 * Constructor that auto-generates a new Predicate where the only condition
	 * for replacing a block with this one is that the other block is air
	 *
	 * @param golemIn  the GolemBase to use
	 * @param stateIn  the IBlockState that will be placed every {@code interval} ticks
	 * @param interval ticks between placing block
	 * @param configAllows whether this AI is enabled by the config
	 **/
	public EntityAIUtilityBlock(final GolemBase golemIn, final IBlockState stateIn, final int interval, boolean configAllows) {
		this(golemIn, stateIn, interval, configAllows, getDefaultBiPred(stateIn));
	}

	@Override
	public boolean shouldExecute() {
		return this.configAllows;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void updateTask() {
		long tickMod = this.golem.ticksExisted % this.tickDelay;
		if (this.configAllows && tickMod == (long) 0) {
			final int x = MathHelper.floor_double(golem.posX);
			final int y = MathHelper.floor_double(golem.posY - 0.20000000298023224D - golem.getYOffset());
			final int z = MathHelper.floor_double(golem.posZ);
			final BlockPos blockPosIn = new BlockPos(x, y, z);
			// test the predicate against each BlockPos in a vertical column around this golem
			// when it passes, place the block and return
			for (int i = 0; i < 3; i++) {
				BlockPos temp = blockPosIn.up(i);
				final IBlockState cur = golem.getEntityWorld().getBlockState(temp);
				// if there's already a matching block, stop here
				if(cur.getBlock() == stateToPlace.getBlock()) {
					return;
				}
				if (this.predicate.test(golem, cur)) {
					this.golem.getEntityWorld().setBlockState(temp, getStateToPlace(cur), 2 | 4);
					return;
				}
			}
		}
	}

	@Override
	public void startExecuting() {
		this.updateTask();
	}
	
	/**
	 * Builds a BiPredicate that returns True only if the Block to replace is AIR
	 * @param stateIn the state that will replace the given one if possible
	 **/
	public static BiPredicate<GolemBase, IBlockState> getDefaultBiPred(final IBlockState stateIn) {
		return new BiPredicate<GolemBase, IBlockState>() {
			@Override
			public boolean test(GolemBase golem, IBlockState toReplace) {
				return toReplace.getBlock() == Blocks.AIR;
			}
		};
	}
	
	protected  IBlockState getStateToPlace(final IBlockState toReplace) {
		return stateToPlace;
	}
}
