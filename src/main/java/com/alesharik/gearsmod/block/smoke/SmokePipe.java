/*
 *     This file is part of GearsMod.
 *
 *     GearsMod is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GearsMod is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with GearsMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alesharik.gearsmod.block.smoke;

import com.alesharik.gearsmod.block.BlockPipe;
import com.alesharik.gearsmod.capability.ConnectionProperties;
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.tileEntity.smoke.SmokePipeTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public final class SmokePipe extends BlockPipe {
    private static final Map<EnumFacing, IProperty<Boolean>> CONNECTION_PROPERTIES_MAP = ConnectionProperties.CONNECTIONS;

    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(4 / 16D, 10 / 16D, 4 / 16D, 12 / 16D, 14 / 16D, 12 / 16D);
    private static final AxisAlignedBB SIDE_CONNECTION_AABB = new AxisAlignedBB(12 / 16D, 10 / 16D, 12 / 16D, 16 / 16D, 14 / 16D, 12 / 16D);
    private static final AxisAlignedBB BOTTOM_CONNECTION_AABB = new AxisAlignedBB(4 / 16D, 0 / 16D, 4 / 16D, 12 / 16D, 10 / 16D, 12 / 16D);
    private static final AxisAlignedBB TOP_CONNECTION_AABB = new AxisAlignedBB(4 / 16D, 12 / 16D, 4 / 16D, 12 / 16D, 16 / 16D, 12 / 16D);

    public SmokePipe() {
        super(Material.ROCK);

        setUnlocalizedName("smoke_pipe");
        setRegistryName("smoke_pipe");
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new SmokePipeTileEntity();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = state.getActualState(source, pos);
        AxisAlignedBB ret = BASE_AABB;
        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            if(actual.getValue(entry.getValue())) {
                EnumFacing facing = entry.getKey();
                switch (facing) {
                    case DOWN:
                        ret = ret.union(BOTTOM_CONNECTION_AABB);
                        break;
                    case UP:
                        ret = ret.union(TOP_CONNECTION_AABB);
                        break;
                    case EAST:
                        ret = ret.union(SIDE_CONNECTION_AABB);
                        break;
                    case WEST:
                        ret = ret.union(new AxisAlignedBB(1 - SIDE_CONNECTION_AABB.minX, SIDE_CONNECTION_AABB.minY, SIDE_CONNECTION_AABB.minZ,
                                1 - SIDE_CONNECTION_AABB.maxX, SIDE_CONNECTION_AABB.maxY, SIDE_CONNECTION_AABB.maxZ));
                        break;
                    case SOUTH:
                        ret = ret.union(new AxisAlignedBB(SIDE_CONNECTION_AABB.minZ, SIDE_CONNECTION_AABB.minY, SIDE_CONNECTION_AABB.minX,
                                SIDE_CONNECTION_AABB.maxZ, SIDE_CONNECTION_AABB.maxY, SIDE_CONNECTION_AABB.maxX));
                        break;
                    case NORTH:
                        ret = ret.union(new AxisAlignedBB(SIDE_CONNECTION_AABB.minZ, SIDE_CONNECTION_AABB.minY, 1 - SIDE_CONNECTION_AABB.minX,
                                SIDE_CONNECTION_AABB.maxZ, SIDE_CONNECTION_AABB.maxY, 1 - SIDE_CONNECTION_AABB.maxX));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected facing " + facing.name());
                }
            }
        }
        return ret;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isPistonMoving) {
        IBlockState actual = state.getActualState(worldIn, pos);

        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);

        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            if(actual.getValue(entry.getValue())) {
                EnumFacing facing = entry.getKey();
                switch (facing) {
                    case DOWN:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOTTOM_CONNECTION_AABB);
                        break;
                    case UP:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, TOP_CONNECTION_AABB);
                        break;
                    case EAST:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_CONNECTION_AABB);
                        break;
                    case WEST:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(1 - SIDE_CONNECTION_AABB.minX, SIDE_CONNECTION_AABB.minY, SIDE_CONNECTION_AABB.minZ,
                                1 - SIDE_CONNECTION_AABB.maxX, SIDE_CONNECTION_AABB.maxY, SIDE_CONNECTION_AABB.maxZ));
                        break;
                    case SOUTH:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(SIDE_CONNECTION_AABB.minZ, SIDE_CONNECTION_AABB.minY, SIDE_CONNECTION_AABB.minX,
                                SIDE_CONNECTION_AABB.maxZ, SIDE_CONNECTION_AABB.maxY, SIDE_CONNECTION_AABB.maxX));
                        break;
                    case NORTH:
                        addCollisionBoxToList(pos, entityBox, collidingBoxes, new AxisAlignedBB(SIDE_CONNECTION_AABB.minZ, SIDE_CONNECTION_AABB.minY, 1 - SIDE_CONNECTION_AABB.minX,
                                SIDE_CONNECTION_AABB.maxZ, SIDE_CONNECTION_AABB.maxY, 1 - SIDE_CONNECTION_AABB.maxX));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected facing " + facing.name());
                }
            }
        }
    }

    @Override
    protected boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity != null && tileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, side.getOpposite());
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
}
