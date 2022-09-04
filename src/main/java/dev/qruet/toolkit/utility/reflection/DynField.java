package dev.qruet.toolkit.utility.reflection;

import java.lang.reflect.Field;

/**
 * Dynamic Field
 */
public class DynField<T> {

    private final Field field;
    private final Object instance;

    public DynField(Class<?> clazz, Object instance, String field_name) {
        this.field = Reflections.getField(clazz, field_name);
        this.instance = instance;
    }

    public T get() {
        try {
            return (T) field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Object val) {
        try {
            field.set(instance, val);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
