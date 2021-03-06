package vazkii.patchouli.common.multiblock;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import vazkii.patchouli.api.IMultiblock;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMultiblock implements IMultiblock, IBlockDisplayReader {
	public ResourceLocation id;
	protected int offX, offY, offZ;
	protected int viewOffX, viewOffY, viewOffZ;
	private boolean symmetrical;
	World world;

	private final transient Map<BlockPos, TileEntity> teCache = new HashMap<>();

	@Override
	public IMultiblock offset(int x, int y, int z) {
		return setOffset(offX + x, offY + y, offZ + z);
	}

	public IMultiblock setOffset(int x, int y, int z) {
		offX = x;
		offY = y;
		offZ = z;
		return setViewOffset(x, y, z);
	}

	void setViewOffset() {
		setViewOffset(offX, offY, offZ);
	}

	@Override
	public IMultiblock offsetView(int x, int y, int z) {
		return setViewOffset(viewOffX + x, viewOffY + y, viewOffZ + z);
	}

	public IMultiblock setViewOffset(int x, int y, int z) {
		viewOffX = x;
		viewOffY = y;
		viewOffZ = z;
		return this;
	}

	@Override
	public IMultiblock setSymmetrical(boolean symmetrical) {
		this.symmetrical = symmetrical;
		return this;
	}

	@Override
	public ResourceLocation getID() {
		return id;
	}

	@Override
	public IMultiblock setId(ResourceLocation res) {
		this.id = res;
		return this;
	}

	@Override
	public void place(World world, BlockPos pos, Rotation rotation) {
		setWorld(world);
		simulate(world, pos, rotation, false).getSecond().forEach(r -> {
			BlockPos placePos = r.getWorldPosition();
			BlockState targetState = r.getStateMatcher().getDisplayedState((int) world.getDayTime()).rotate(rotation);
			Block targetBlock = targetState.getBlock();

			if (!targetBlock.isAir(targetState, world, placePos) && targetState.isValidPosition(world, placePos) && world.getBlockState(placePos).getMaterial().isReplaceable()) {
				world.setBlockState(placePos, targetState);
			}
		});
	}

	@Override
	public Rotation validate(World world, BlockPos pos) {
		if (isSymmetrical() && validate(world, pos, Rotation.NONE)) {
			return Rotation.NONE;
		} else {
			for (Rotation rot : Rotation.values()) {
				if (validate(world, pos, rot)) {
					return rot;
				}
			}
		}
		return null;
	}

	@Override
	public boolean validate(World world, BlockPos pos, Rotation rotation) {
		setWorld(world);
		Pair<BlockPos, Collection<SimulateResult>> sim = simulate(world, pos, rotation, false);

		return sim.getSecond().stream().allMatch(r -> r.test(world, rotation));
	}

	@Override
	public boolean isSymmetrical() {
		return symmetrical;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	@Override
	@Nullable
	public TileEntity getTileEntity(BlockPos pos) {
		BlockState state = getBlockState(pos);
		if (state.getBlock().hasTileEntity(state)) {
			return teCache.computeIfAbsent(pos.toImmutable(), p -> state.getBlock().createTileEntity(state, world));
		}
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.getDefaultState();
	}

	@Override
	public float func_230487_a_(Direction direction, boolean shaded) {
		return 1.0F;
	}

	@Override
	public WorldLightManager getLightManager() {
		return null;
	}

	@Override
	public int getBlockColor(BlockPos pos, ColorResolver color) {
		return color.getColor(Biomes.PLAINS, pos.getX(), pos.getZ());
	}

	@Override
	public int getLightFor(LightType type, BlockPos pos) {
		return 15;
	}

	@Override
	public int getLightSubtracted(BlockPos pos, int ambientDarkening) {
		return 15 - ambientDarkening;
	}

}
