package com.golems.items;

import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.main.Config;
import com.golems.util.GolemLookup;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemBedrockGolem extends Item {

	public ItemBedrockGolem() {
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// creative players can use this item to spawn a bedrock golem
		if (GolemLookup.getConfig(EntityBedrockGolem.class).canSpawn()) {
			if (Config.isBedrockGolemCreativeOnly() && !player.capabilities.isCreativeMode) {
				return EnumActionResult.PASS;
			}
			if (facing == EnumFacing.DOWN) {
				return EnumActionResult.FAIL;
			}

			final boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
			final BlockPos spawn = flag ? pos : pos.offset(facing);

			if (!worldIn.isRemote) {
				final GolemBase golem = new EntityBedrockGolem(worldIn);
				golem.setPlayerCreated(true);
				golem.moveToBlockPosAndAngles(spawn, 0.0F, 0.0F);
				worldIn.spawnEntityInWorld(golem);
			}
			spawnParticles(worldIn, pos.getX() - 0.5D, pos.getY() + 1.0D, pos.getZ() - 0.5D, 0.2D);
			player.swingArm(hand);
			if (!player.capabilities.isCreativeMode) {
				stack.stackSize--;
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion) {
		spawnParticles(world, x, y, z, motion, EnumParticleTypes.SMOKE_LARGE, 60);
	}
	
	public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion,
			final EnumParticleTypes type, final int num) {
		if (world.isRemote) {
			for (int i = num + world.rand.nextInt(Math.max(1, num / 2)); i > 0; --i) {
				world.spawnParticle(type, 
						x + world.rand.nextDouble() - 0.5D,
						y + world.rand.nextDouble() - 0.5D, 
						z + world.rand.nextDouble() - 0.5D,
						world.rand.nextDouble() * motion - motion * 0.5D,
						world.rand.nextDouble() * motion * 0.5D,
						world.rand.nextDouble() * motion - motion * 0.5D);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack par1ItemStack, final EntityPlayer player, final List<String> par3List,
				   final boolean advanced) {
		final String loreCreativeOnly = TextFormatting.RED + trans("tooltip.creative_only_item");
		if (Config.isBedrockGolemCreativeOnly()) {
			par3List.add(loreCreativeOnly);
		}

		if (GuiScreen.isShiftKeyDown()) {
			par3List.add(I18n.format("tooltip.use_to_spawn", trans("entity.golems.golem_bedrock.name")));
			par3List.add(
				I18n.format("tooltip.use_on_existing", trans("entity.golems.golem_bedrock.name")));
			par3List.add(trans("tooltip.to_remove_it") + ".");
		} else {
			final String lorePressShift = TextFormatting.GRAY + trans("tooltip.press") + " "
				+ TextFormatting.YELLOW + trans("tooltip.shift").toUpperCase() + " "
				+ TextFormatting.GRAY + trans("tooltip.for_more_details");
			par3List.add(lorePressShift);
		}
	}

	@SideOnly(Side.CLIENT)
	private static String trans(final String s) {
		return I18n.format(s);
	}
}
