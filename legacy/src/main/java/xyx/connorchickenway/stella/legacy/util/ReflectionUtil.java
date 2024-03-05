/*
 *     Stella - A tablist API
 *     Copyright (C) 2024  ConnorChickenway
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package xyx.connorchickenway.stella.legacy.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

    private ReflectionUtil(){}

    public static Field getField(Object clazz, String fieldName) {
        try {
            Field field = clazz.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }catch (Exception ignore) {
        }
        return null;
    }

    public static void set(Object clasz, String fieldName, Object value) {
        try {
            Field field = getField(clasz, fieldName);
            if (field != null)
                field.set(clasz, value);
        }catch (Exception ignore) {
        }
    }

    public static Object get(Object clasz, String fieldName) {
        try {
            Field field = getField(clasz, fieldName);
            if (field != null)
                return field.get(clasz);
        }catch (Exception ignore) {
        }
        return null;
    }


}
