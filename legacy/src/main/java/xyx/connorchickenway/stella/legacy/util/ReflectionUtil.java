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
