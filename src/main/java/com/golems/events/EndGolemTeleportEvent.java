package com.golems.events;

import com.golems.entity.EntityEndstoneGolem;

import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * Fired when an Endstone Golem (or child class of such) tries to teleport. The destination can be
 * modified, or the event can be canceled entirely.
 *
 * @see EnderTeleportEvent
 **/
@Cancelable
public class EndGolemTeleportEvent extends EnderTeleportEvent {

	public final EntityEndstoneGolem entityGolem;

	public EndGolemTeleportEvent(final EntityEndstoneGolem entity, final double targetX, final double targetY,
				     final double targetZ, final float attackDamage) {
		super(entity, targetX, targetY, targetZ, attackDamage);
		this.entityGolem = entity;
	}
}
