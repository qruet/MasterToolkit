package dev.qruet.toolkit.utility.reflection;

import java.lang.reflect.Field;

public class BoolField {

    private final Field field;
    private final Object instance;

    public BoolField(Class<?> clazz, Object instance, String field_name) {
        this.field = Reflections.getField(clazz, field_name);
        this.instance = instance;
    }

    public boolean get() {
        try {
            return (boolean) field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void set(boolean val) {
        try {
            field.set(instance, val);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
