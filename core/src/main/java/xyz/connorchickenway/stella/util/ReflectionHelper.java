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

package xyz.connorchickenway.stella.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class ReflectionHelper {

    private ReflectionHelper() {}

    public static Field getDeclaredField(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Field field, Object classObject, Object object) {
        try {
            field.set(classObject, object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(Field field, Object classObject) {
        try {
            return field.get(classObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object newInstance(Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeConstructor(Constructor<?> constructor, Object... objects) {
        try {
            return constructor.newInstance(objects);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeStaticMethod(Method method, Object... objs) {
        return invokeMethod(method, null, objs);
    }

    public static Object invokeMethod(Method method,Object obj, Object... objs) {
        try {
            return method.invoke(obj, objs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getStaticField(Class<?> cls, String fieldName) {
        try {
            return cls.getField(fieldName).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Method ADD, ADD_ALL, ENUM_SET_NONE_OF;

    static {
        try {
            Class<?> collectionClass = Collection.class;
            ADD = collectionClass.getMethod("add", Object.class);
            ADD_ALL = collectionClass.getMethod("addAll", Collection.class);
            if (NMSVersion.isMajor()) {
                Class<?> enumSetClass = Class.forName("java.util.EnumSet");
                ENUM_SET_NONE_OF = enumSetClass.getMethod("noneOf", Class.class);
            }
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}