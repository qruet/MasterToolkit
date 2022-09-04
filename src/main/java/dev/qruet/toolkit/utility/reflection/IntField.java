package dev.qruet.toolkit.utility.reflection;

import java.lang.reflect.Field;

public class IntField {

    private final Field field;
    private final Object instance;

    public IntField(Class<?> clazz, Object instance, String field_name) {
        this.field = Reflections.getField(clazz, field_name);
        this.instance = instance;
    }

    public int get() {
        try {
            return (int) field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void set(int val) {
        try {
            field.set(instance, val);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
