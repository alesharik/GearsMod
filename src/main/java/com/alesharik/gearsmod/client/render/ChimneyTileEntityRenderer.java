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

package com.alesharik.gearsmod.client.render;

import com.alesharik.gearsmod.gl.model.OBJModel;
import com.alesharik.gearsmod.tileEntity.smoke.ChimneyTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL11.*;

@Deprecated
@SideOnly(Side.CLIENT)
public final class ChimneyTileEntityRenderer extends TileEntitySpecialRenderer<ChimneyTileEntity> {
    private static final OBJModel model;
    //    private static final Texture textureMap;
    private static final ResourceLocation textureMap;

    static {
        try {
            InputStream stream = ChimneyTileEntityRenderer.class.getClassLoader().getResourceAsStream("assets/gearsmod/models/block/smoke/chimney.obj");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nRead;
            byte[] buffer = new byte[4096];
            while((nRead = stream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, nRead);
            }
            stream.close();
            String file = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
            model = new OBJModel(file.split("\n"));

//            BufferedImage bufferedImage = ImageIO.read(ChimneyTileEntityRenderer.class.getClassLoader().getResourceAsStream("assets/gearsmod/textures/blocks/chimney_block_texturemap.png"));
//            textureMap = new Texture(bufferedImage);
            textureMap = new ResourceLocation("gearsmod", "textures/blocks/smoke/chimney_block_texturemap.png");
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    public void renderTileEntityAt(ChimneyTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        glPushMatrix();

        glTranslated(x, y, z);

//        GlStateManager.disableLighting();
//        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        glTranslatef(0.5F, 1.2F, 0.5F);
        glScalef(0.5F, 0.5F, 0.5F);

//        GlStateManager.enableBlend();

        bindTexture(textureMap);
//        GlStateManager.disableCull();
        model.render();
        glPopMatrix();

//        GlStateManager.enableCull();
    }
}
