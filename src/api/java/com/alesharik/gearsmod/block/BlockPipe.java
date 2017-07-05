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

package com.alesharik.gearsmod.block;

import com.alesharik.gearsmod.capability.ConnectionProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public abstract class BlockPipe extends BlockContainer {
    private static final Map<EnumFacing, IProperty<Boolean>> CONNECTION_PROPERTIES_MAP = ConnectionProperties.CONNECTIONS;

    public BlockPipe(Material material) {
        super(material);
    }

    /**
     * @param world
     * @param pos   neighbour block pos
     * @param side  your side
     * @return
     */
    protected abstract boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing side);

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @ParametersAreNonnullByDefault
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return !blockState.getValue(CONNECTION_PROPERTIES_MAP.get(side));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECTION_PROPERTIES_MAP.values().toArray(new IProperty[6]));
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            state = state.withProperty(entry.getValue(), canConnect(worldIn, pos.offset(entry.getKey()), entry.getKey()));
        }
        worldIn.setBlockState(pos, state);
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.getDefaultState();

        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            state = state.withProperty(entry.getValue(), canConnect(worldIn, pos.offset(entry.getKey()), entry.getKey()));
        }
        return state;
    }
//
//    @Override
//    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
//        for(EnumFacing side : CONNECTION_PROPERTIES_MAP.keySet()) {
//            state = state.withProperty(CONNECTION_PROPERTIES_MAP.get(side),
//                    canConnect(worldIn, pos.offset(side), side));
//        }
//        worldIn.setBlockState(pos, state);
//    }
//
//    @Override
//    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
//        for(EnumFacing side : CONNECTION_PROPERTIES_MAP.keySet()) {
//            state = state.withProperty(CONNECTION_PROPERTIES_MAP.get(side),
//                    canConnect(worldIn, pos.offset(side), side));
//        }
//        worldIn.setBlockState(pos, state);
//    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        for(Map.Entry<EnumFacing, IProperty<Boolean>> entry : CONNECTION_PROPERTIES_MAP.entrySet()) {
            state = state.withProperty(entry.getValue(), canConnect(worldIn, pos.offset(entry.getKey()), entry.getKey()));
        }
        return state;
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, @Nonnull BlockPos pos, int id, int param) {
        boolean b = super.eventReceived(state, worldIn, pos, id, param);
        onBlockAdded(worldIn, pos, state);
        return b;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}
