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

package com.alesharik.gearsmod.block.steam;

import com.alesharik.gearsmod.block.BlockPipe;
import com.alesharik.gearsmod.capability.ConnectionProperties;
import com.alesharik.gearsmod.capability.steam.SteamCapability;
import com.alesharik.gearsmod.material.ModMaterials;
import com.alesharik.gearsmod.steam.SteamStorageProvider;
import com.alesharik.gearsmod.tileEntity.steam.BasicSteamPipeTileEntity;
import com.alesharik.gearsmod.util.RotationUtils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

public final class BasicSteamPipe extends BlockPipe {
    private static final Map<EnumFacing, IProperty<Boolean>> CONNECTION_PROPERTIES_MAP = ConnectionProperties.CONNECTIONS;
    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(4 / 16D, 4 / 16D, 4 / 16D, 12 / 16D, 12 / 16D, 12 / 16D);
    private static final AxisAlignedBB SIDE_CONNECTION_AABB = new AxisAlignedBB(4 / 16D, 0 / 16D, 4 / 16D, 12 / 16D, 4 / 16D, 12 / 16D);

    public BasicSteamPipe() {
        super(ModMaterials.BRASS_MATERIAL);
        setUnlocalizedName("basic_steam_pipe");
        setRegistryName("basic_steam_pipe");
    }

    @Override
    protected boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tileEntity = world.getTileEntity(pos);
        EnumFacing opposite = side.getOpposite();
        if(tileEntity == null)
            return false;

        if(tileEntity instanceof SteamStorageProvider) {
            EnumFacing[] facing = ((SteamStorageProvider) tileEntity).getConnectedFacing();
            for(EnumFacing enumFacing : facing) {
                if(enumFacing == opposite)
                    return true;
            }
        }
        return tileEntity.hasCapability(SteamCapability.CAPABILITY, opposite);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new BasicSteamPipeTileEntity();
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity != null && tileEntity instanceof BasicSteamPipeTileEntity)
            ((BasicSteamPipeTileEntity) tileEntity).onRemove();
        super.breakBlock(worldIn, pos, state);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        IBlockState actual = state.getActualState(source, pos);
        AxisAlignedBB ret = BASE_AABB;
        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            if(actual.getValue(entry.getValue())) {
                EnumFacing facing = entry.getKey();
                ret = ret.union(RotationUtils.rotateAABB(SIDE_CONNECTION_AABB, facing));
            }
        }
        return ret;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isPistonMoving) {
        IBlockState actual = state.getActualState(worldIn, pos);

        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);

        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            if(actual.getValue(entry.getValue())) {
                EnumFacing facing = entry.getKey();
                addCollisionBoxToList(pos, entityBox, collidingBoxes, RotationUtils.rotateAABB(SIDE_CONNECTION_AABB, facing));
            }
        }
    }

    @ParametersAreNonnullByDefault
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }
}
