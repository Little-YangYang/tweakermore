package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RespawnAnchorHandler extends AbstractBlockHandler
{
	public RespawnAnchorHandler(World world, BlockPos blockPos, BlockState blockState)
	{
		super(world, blockPos, blockState);
	}

	@Override
	public boolean isValid()
	{
		// ref: net.minecraft.block.RespawnAnchorBlock.onUse
		if (blockState.getBlock() instanceof RespawnAnchorBlock)
		{
			return !RespawnAnchorBlock.isNether(world);
		}
		return false;
	}

	@Override
	public Vec3d getExplosionCenter()
	{
		return PositionUtil.centerOf(this.blockPos);
	}

	@Override
	public BlockPos getDeduplicationKey()
	{
		return this.blockPos;
	}

	@Override
	public void addBlocksToRemove(TemporaryBlockReplacer replacer)
	{
		replacer.add(this.blockPos, Blocks.AIR.getDefaultState());
	}

	@Override
	public float getExplosionPower()
	{
		return 5.0F;
	}
}
