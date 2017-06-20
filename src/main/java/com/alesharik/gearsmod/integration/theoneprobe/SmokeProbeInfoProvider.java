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
import com.alesharik.gearsmod.capability.smoke.SmokeCapability;
import com.alesharik.gearsmod.capability.smoke.SmokeStorage;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.awt.Color;

public class SmokeProbeInfoProvider implements IProbeInfoProvider {
    private static final int progressBarDefaultColor = new Color(51, 51, 51).getRGB();
    private static final int progressBarOverloadedColor = new Color(122, 13, 0).getRGB();

    @Override
    public String getID() {
        return GearsMod.MODID + ":smokeProvider";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if(tileEntity != null && tileEntity.hasCapability(SmokeCapability.DEFAULT_CAPABILITY, null)) {
            SmokeStorage smokeStorage = tileEntity.getCapability(SmokeCapability.DEFAULT_CAPABILITY, null);
            if(smokeStorage == null)
                return;

            IProgressStyle progressStyle = new ProgressStyle()
                    .prefix("Smoke: ");
            if(smokeStorage.overloaded())
                progressStyle.filledColor(progressBarOverloadedColor);
            else
                progressStyle.filledColor(progressBarDefaultColor);

            probeInfo.progress(smokeStorage.getSmokeAmount(), smokeStorage.getMaxSmokeAmount(), progressStyle);

            if(mode == ProbeMode.EXTENDED) {
                probeInfo.text("Smoke: " + smokeStorage.getSmokeAmount() + " of " + smokeStorage.getMaxSmokeAmount());
                return;
            }

            if(mode == ProbeMode.DEBUG) {
                probeInfo.text("Smoke: " + smokeStorage.getSmokeAmount() + " of " + smokeStorage.getMaxSmokeAmount() + (smokeStorage.overloaded() ? ". Overloaded!" : ""));
                probeInfo.text("Can extract: " + smokeStorage.canExtract() + ", can receive: " + smokeStorage.canReceive());
            }
        }
    }
}
