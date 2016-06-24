package com.golems.util;

import java.util.List;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WeightedItem 
{
	public final Item item;
	public final int meta;
	public final int maxAmount;
	public final int minAmount;
	public final int dropChance;
		
	public WeightedItem(Item itemIn, int metadata, int min, int max, int percentChance)
	{
		this.item = itemIn;
		this.meta = metadata;
		this.minAmount = min;
		this.maxAmount = max;
		this.dropChance = percentChance > 100 ? 100 : percentChance;
	}
	
	public WeightedItem(ItemStack stack, int percentChance)
	{
		this(stack.getItem(), stack.getMetadata(), stack.stackSize, stack.stackSize, percentChance);
	}
	
	/** Calculated randomly each time this method is called **/
	public boolean shouldDrop(Random rand)
	{
		return this.item != null && rand.nextInt(100) < this.dropChance;
	}
	
	/** Gets a random number between minAmount and maxAmount, inclusive **/
	public int getRandomSize(Random rand)
	{
		return this.maxAmount > this.minAmount ? this.minAmount + rand.nextInt(this.maxAmount - this.minAmount + 1) : this.minAmount;
	}
	
	/** Makes an ItemStack of this WeightedItem using {@link #getRandomSize(Random)} **/
	public ItemStack makeStack(Random rand)
	{
		int size = getRandomSize(rand);
		return new ItemStack(this.item, size, this.meta);
	}

	/** 
	 * Iterates through a list of drops and removes any entries that contain the given item and metadata.
	 * Useful for removing the default drops, which include redstone, yellow flowers, and red flowers.
	 * Pass {@code OreDictionary.WILDCARD_VALUE} to ignore metadata.
	 **/
	public static boolean removeFromList(List<WeightedItem> list, Item in, int meta)
	{
		boolean flag = false;
		for(WeightedItem w : list)
		{
			if(w.item == in && (meta == OreDictionary.WILDCARD_VALUE || w.meta == meta))
			{
				list.remove(w);
				flag = true;
			}
		}
		return flag;
	}
}
