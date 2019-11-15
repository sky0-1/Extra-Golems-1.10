package com.golems.entity;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityBoneGolem extends GolemBase {

	public EntityBoneGolem(final World world) {
		super(world);
		this.setCanTakeFallDamage(true);
		this.setLootTableLoc(GolemNames.BONE_GOLEM);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	protected ResourceLocation applyTexture() {
		//return makeGolemTexture("bone");
		return makeTexture(ExtraGolems.MODID, GolemNames.BONE_GOLEM + "_skeleton");
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
