package com.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.golems.events.IceGolemFreezeEvent;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public final class EntityIceGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Freeze Blocks";
	public static final String CAN_USE_REGULAR_ICE = "Can Use Regular Ice";
	public static final String AOE = "Area of Effect";

	public EntityIceGolem(final World world) {
		super(world);
		this.setCanSwim(true); // just in case
		this.setLootTableLoc(GolemNames.ICE_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.ICE_GOLEM);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// calling every other tick reduces lag by 50%
		if (this.ticksExisted % 2 == 0) {
			final int x = MathHelper.floor_double(this.posX);
			final int y = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			final int z = MathHelper.floor_double(this.posZ);
			final BlockPos below = new BlockPos(x, y, z);

			if (this.worldObj.getBiome(below).getTemperature() > 1.0F) {
				this.attackEntityFrom(DamageSource.onFire, 1.0F);
			}
			GolemConfigSet cfg = getConfig(this);
			if (cfg.getBoolean(ALLOW_SPECIAL)) {
				final IceGolemFreezeEvent event = new IceGolemFreezeEvent(this, below,
					cfg.getInt(AOE));
				if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Result.DENY) {
					this.freezeBlocks(event.getAffectedPositions(), event.getFunction(),
						event.updateFlag);
				}
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (entity.isBurning()) {
				this.attackEntityFrom(DamageSource.generic, 0.5F);
			}
			return true;
		}
		return false;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	/**
	 * Usually called after creating and firing a {@link IceGolemFreezeEvent}. Iterates through the
	 * list of positions and calls {@code apply(IBlockState input)} on the passed
	 * {@code Function<IBlockState, IBlockState>} .
	 *
	 * @return whether all setBlockState calls were successful.
	 **/
	public boolean freezeBlocks(final List<BlockPos> positions,
				    final Function<IBlockState, IBlockState> function, final int updateFlag) {
		boolean flag = false;
		for (int i = 0, len = positions.size(); i < len; i++) {
			final BlockPos pos = positions.get(i);
			final IBlockState currentState = this.worldObj.getBlockState(pos);
			final IBlockState toSet = function.apply(currentState);
			if (toSet != null && toSet != currentState) {
				flag &= this.worldObj.setBlockState(pos, toSet, updateFlag);
			}
		}
		return flag;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntityIceGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.AQUA + trans("entitytip.freezes_blocks"));
		}
		return list;
	}
}
