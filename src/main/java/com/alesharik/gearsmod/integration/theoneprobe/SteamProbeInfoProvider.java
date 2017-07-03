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

package com.alesharik.gearsmod.integration.theoneprobe;

import com.alesharik.gearsmod.GearsMod;
import com.alesharik.gearsmod.capability.steam.SteamCapability;
import com.alesharik.gearsmod.capability.steam.SteamStorage;
import com.alesharik.gearsmod.steam.SteamNetwork;
import com.alesharik.gearsmod.steam.SteamStorageProvider;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.awt.Color;

public class SteamProbeInfoProvider implements IProbeInfoProvider {
    private static final int progressBarDefaultColor = new Color(51, 51, 51).getRGB();

    @Override
    public String getID() {
        return GearsMod.MODID + ":steamProvider";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if(tileEntity == null)
            return;

        SteamNetwork network = null;
        if(tileEntity instanceof SteamStorageProvider)
            network = ((SteamStorageProvider) tileEntity).getSteamStorage().getNetwork();
        else if(tileEntity.hasCapability(SteamCapability.CAPABILITY, null)) {
            SteamStorage capability = tileEntity.getCapability(SteamCapability.CAPABILITY, null);
            if(capability != null)
                network = capability.getNetwork();
        }
        if(network == null)
            return;

        ProgressStyle style = new ProgressStyle();
        style.backgroundColor(progressBarDefaultColor);
        style.alternateFilledColor(progressBarDefaultColor);

        probeInfo.progress(Math.round(network.getPressure()), Math.round(network.getPressure()), style);
    }
}
