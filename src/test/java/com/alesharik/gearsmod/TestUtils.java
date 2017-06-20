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

package com.alesharik.gearsmod;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class TestUtils {
    /**
     * Utility class is class, where is private no-args constructor, which throw {@link UnsupportedOperationException} and only static methods
     *
     * @param clazz the class to test
     */
    public static void assertUtilityClass(@Nonnull Class<?> clazz) {
        for(Constructor constructor : clazz.getDeclaredConstructors()) {
            if(Modifier.isPrivate(constructor.getModifiers())) {
                if(constructor.getParameterCount() == 0) {
                    try {
                        constructor.setAccessible(true);
                        constructor.newInstance();
                        throw new AssertionError("Constructor must throw UnsupportedOperationException!");
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new AssertionError(e);
                    } catch (InvocationTargetException e) {
                        if(!(e.getCause() instanceof UnsupportedOperationException)) {
                            throw new AssertionError("Constructor must throw UnsupportedOperationException!");
                        }
                    }
                } else
                    throw new AssertionError("Utility class must have no constructors with parameters!");
            } else
                throw new AssertionError("Utility class must have no pubic/protected/package-private constructors!");
        }
        for(Method method : clazz.getDeclaredMethods())
            if(!Modifier.isStatic(method.getModifiers()))
                throw new AssertionError("All methods in utility class must be static!");

        for(Field field : clazz.getDeclaredFields())
            if(!Modifier.isStatic(field.getModifiers()))
                throw new AssertionError("All fields in utility class must be static!");
    }
}
