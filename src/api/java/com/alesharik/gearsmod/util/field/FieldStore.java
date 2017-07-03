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

package com.alesharik.gearsmod.util.field;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface FieldStore extends INBTSerializable<NBTTagCompound> {
    /**
     * Set field value. Operation must update other connected stores. If field not found, ut create a new one, overwise
     * replace old value by new value
     *
     * @param id    field id
     * @param field field value
     */
    void setField(int id, int field);

    /**
     * Get field form store
     *
     * @param id field id
     * @return field value
     * @throws FieldNotFoundException if field not found
     */
    int getField(int id);

    /**
     * Check field
     *
     * @param id field id
     * @return <code>true</code> if field found, overwise <code>false</code>
     */
    boolean containsField(int id);

    /**
     * Delete field from store. Operation must update other connected stores.
     *
     * @param id field id
     */
    void deleteField(int id);

    default int getFieldOrDefault(int id, int def) {
        if(containsField(id))
            return getField(id);
        return def;
    }

    /**
     * Operation must update other connected stores.
     */
    void sync();

    void setWorld(World world);

    void setBlockPos(BlockPos pos);
}
