package com.loficostudios.forgified.paper.gui.unnamed;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Experimental
public class Property<T> {
    private String name;
    private Class<T> clazz;
    private Supplier<T> get;
    private Consumer<T> set;
    private Object target;
    private boolean mutable;
    private boolean nullable;

    protected Property(String name, Object target, Class<T> clazz, boolean mutable, boolean nullable, Supplier<T> get, Consumer<T> set) {
        this.name = name;
        this.mutable = mutable;
        this.clazz = clazz;
        this.get = get;
        this.nullable = nullable;
        this.set = set;
        this.target = target;
    }

    public boolean mutable() {
        return mutable;
    }

    public String name() {
        return name;
    }

    public Object target() {
        return target;
    }

    public Class<T> clazz() {
        return clazz;
    }

    public boolean nullable() {
        return nullable;
    }

    public void set(T value) {
        if (!mutable)
            throw new RuntimeException(new IllegalAccessException("Property is not mutable"));
        set.accept(value);
    }

    public @Nullable T get() {
        return get.get();
    }

    private static Class<?> clazz(Field field) {
        Class<?> clazz = field.getType();
        if (clazz.isPrimitive()) {
            if (clazz == int.class) clazz = Integer.class;
            else if (clazz == double.class) clazz = Double.class;
            else if (clazz == float.class) clazz = Float.class;
            else if (clazz == boolean.class) clazz = Boolean.class;
            else if (clazz == long.class) clazz = Long.class;
            else if (clazz == short.class) clazz = Short.class;
            else if (clazz == byte.class) clazz = Byte.class;
            else if (clazz == char.class) clazz = Character.class;
        }
        return clazz;
    }

    public static <T> Property<T> from(Object obj, Field field) {
        var raw = clazz(field);
        @SuppressWarnings("unchecked")
        var clazz = (Class<T>) raw;

        var configurable = field.getAnnotation(Configurable.class);
        if (configurable == null) {
            throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with @" + Configurable.class.getSimpleName());
        }

        return new Property<>(configurable.name() == null || configurable.name().isEmpty() ? field.getName() : configurable.name(), obj, clazz, configurable.mutable(), configurable.nullable(), () -> {
            try {
                field.setAccessible(true);
                return clazz.cast(field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, (value) -> {
            try {
                field.setAccessible(true);
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static List<Property<?>> getProperties(Object target) {
        return getProperties(target, true);
    }

    public static List<Property<?>> getProperties(Object target, boolean includeInherited) {
        List<Property<?>> result = new LinkedList<>();
        Class<?> currentClass = target.getClass();
        if (includeInherited) {
            while (currentClass != null && currentClass != Object.class) {
                for (Field field : currentClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Configurable.class)) {
                        result.add(Property.from(target, field));
                    }
                }

                currentClass = currentClass.getSuperclass();
            }
        } else {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Configurable.class)) {
                    result.add(Property.from(target, field));
                }
            }
        }

        return result;
    }

}
