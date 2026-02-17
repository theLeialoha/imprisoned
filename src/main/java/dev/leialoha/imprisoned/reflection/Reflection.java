package dev.leialoha.imprisoned.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@SuppressWarnings("CallToPrintStackTrace")
public class Reflection {

    public static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException ignored) {}

        return false;
    }

    public static boolean classAnyExists(String... classNames) {
        int size = Stream.of(classNames)
            .filter(className -> classExists(className))
            .toArray().length;

        return size > 0;
    }

    public static boolean classAllExists(String... classNames) {
        int size = Stream.of(classNames)
            .filter(className -> classExists(className))
            .toArray().length;

        return size == classNames.length;
    }


    public static Class<?> toArrayClass(Class<?> clazz) {
        try {
            return Class.forName("[L" + clazz.getName() + ';');
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static <T> T createInstance(Class<T> clazz, Object ...args) {
        if (clazz == null) return null;
        
        try {
            Class<?>[] methodArgs = getClasses(args);
            Constructor<T> constructor = clazz.getConstructor(methodArgs);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T createInstance(Constructor<T> constructor, Object ...args) {
        if (constructor == null) return null;
        
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(Object obj, String fieldName, Class<?> clazz) {
        if (obj == null) return null;

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldByType(Object obj, Class<?> clazz, Class<T> type) {
        if (obj == null) return null;

        try {
            Field[] fields = clazz.getDeclaredFields();

            Field field = Stream.of(fields)
                .filter(f -> f.getType().equals(type))
                .findFirst().get();

            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (NoSuchElementException e) {
            NoSuchFieldException noField = new NoSuchFieldException();
            noField.addSuppressed(e);
            noField.printStackTrace();
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setField(Object obj, String fieldName, Class<?> clazz, Object value) {
        if (obj == null) return;

        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public static void setField(Object obj, String fieldName, Object value) {
        if (obj == null) return;

        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public static Object cast(Object obj, Class<?> clazz) {
        if (obj == null) return null;

        try {
            return clazz.cast(obj);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callMethod(Object obj, String methodName, Object ...args) {
        if (obj == null) return null;

        try {
            Class<?> clazz = obj.getClass();
            Class<?>[] methodArgs = getClasses(args);
            Method method = getMethod(clazz, methodName, methodArgs);
            if (method == null) return null;

            return method.invoke(obj, args);
        } catch (NoSuchMethodError | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?> ...args) {
        if (clazz == null) return null;

        try {
            Method method = clazz.getMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodError | SecurityException | NoSuchMethodException | IllegalArgumentException  e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callStaticMethod(Class<?> clazz, String methodName, Object ...args) {
        if (clazz == null) return null;

        try {
            Class<?>[] methodArgs = getClasses(args);
            Method method = clazz.getMethod(methodName, methodArgs);
            method.setAccessible(true);

            return method.invoke(null, args);
        } catch (NoSuchMethodError | SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?>[] getClasses(Object ...args) {
        return Stream.of(args).map(Object::getClass).toArray(Class<?>[]::new);
    }

}
