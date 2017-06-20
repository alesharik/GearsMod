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

import com.alesharik.gearsmod.integration.IntegrationModule;
import com.alesharik.gearsmod.integration.IntegrationModuleInfo;
import com.alesharik.gearsmod.integration.IntegrationSide;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TheOneProbeIntegrationModule implements IntegrationModule, Function<ITheOneProbe, Void> {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void preInit() {
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", this.getClass().getName());
    }

    @Override
    public void init() {
    }

    @Override
    public void postInit() {
    }

    @Nonnull
    @Override
    public IntegrationModuleInfo getInfo() {
        return Info.INSTANCE;
    }

    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe input) {
        if(input == null)
            return null;

        input.registerProvider(new SmokeProbeInfoProvider());

        return null;
    }

    private static final class Info implements IntegrationModuleInfo {
        private static final Info INSTANCE = new Info();

        @Nonnull
        @Override
        public String getModId() {
            return "theoneprobe";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "The One Probe";
        }

        @Override
        public IntegrationSide getSide() {
            return IntegrationSide.BOTH;
        }
    }
}
