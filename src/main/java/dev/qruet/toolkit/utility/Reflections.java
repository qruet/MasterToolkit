package dev.qruet.toolkit.utility;

import org.bukkit.Bukkit;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class Reflections {

    private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<>();

    private static Class<?> getPrimitiveType(Class<?> clazz) {
        return CORRESPONDING_TYPES.containsKey(clazz) ? CORRESPONDING_TYPES.get(clazz) : clazz;
    }

    private static final VarHandle MODIFIERS;
    private static final ClassLoader LOADER;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }

        LOADER = Thread.currentThread().getContextClassLoader();
    }

    public static void makeNonFinal(Field field) {
        int mods = field.getModifiers();
        if (Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }
    }

    private static Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
        int a = classes != null ? classes.length : 0;
        Class<?>[] types = new Class<?>[a];
        for (int i = 0; i < a; i++) {
            types[i] = getPrimitiveType(classes[i]);
        }
        return types;
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (!a[i].equals(o[i]) && !a[i].isAssignableFrom(o[i]))
                return false;
        return true;
    }

    private static Object getHandle(Object obj) {
        try {
            return getMethod("getHandle", obj.getClass()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param method Method to be invoked
     * @param obj    Instance of class where method exists
     * @return Returns any objects that the method may return
     */
    public static Object invokeMethod(String method, Object obj) {
        try {
            return getMethod(method, obj.getClass()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calls a method
     *
     * @param method Method to be invoked
     * @param obj    Instance of the class where method exists
     * @param args   Arguments in the method to be passed
     * @return Returns any objects that the method may return
     */
    public static Object invokeMethodWithArgs(String method, Object obj, Object... args) {
        try {
            return getMethod(method, obj.getClass()).invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a method instance
     *
     * @param name       Name of method
     * @param clazz      Class where method exists
     * @param paramTypes Parameters the class may have
     * @return Instance of method
     */
    public static Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes) {
        Class<?>[] t = toPrimitiveTypeArray(paramTypes);
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = toPrimitiveTypeArray(m.getParameterTypes());
            if (m.getName().equals(name) && equalsTypeArray(types, t))
                return m;
        }
        return null;
    }

    /**
     * Get version of server
     *
     * @return String version
     */
    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Converts string name of class to Class object
     *
     * @param className Name of class
     * @return Class object
     */
    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * Use this instead of getNMSClass for classes in the craftbukkit package
     *
     * @param className Name of class
     * @return Class Object
     */
    public static Class<?> getCraftBukkitClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * Retrieve fields from classes
     *
     * @param clazz Class where field exists
     * @param name  Name of field
     * @return Field object
     */
    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            makeNonFinal(field);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieve method instance
     *
     * @param clazz Name of class where method exists
     * @param name  Name of method
     * @param args  Parameters that method may have
     * @return Method instance
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Method m : clazz.getDeclaredMethods())
            if (m.getName().equals(name)
                    && (args.length == 0 || ClassListEqual(args,
                    m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++)
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        return equal;
    }

    public static LinkedList<String> getSubPackagePaths(String packageName) {
        final LinkedList<String> ret = new LinkedList<>();

        try {
            boolean f1;
            do {
                f1 = false;
                Enumeration<URL> res = LOADER.getResources(packageName.replace('.', '/'));

                while (res.hasMoreElements()) {
                    final String dirPath = URLDecoder.decode(res.nextElement().getPath(), "UTF-8");
                    final File dir = new File(dirPath);

                    if (dir.listFiles() != null) {
                        for (final File file : dir.listFiles()) {
                            if (file.isDirectory()) {
                                packageName = !packageName.isBlank() ? (packageName + '.' + file.getName()) : file.getName();
                                f1 = ret.add(packageName);
                            }
                        }
                    }
                }
            } while (f1);
        } catch (IOException e) {
            return new LinkedList<>();
        }

        return ret;
    }

    public static List<Class> findAllClassesInPackage(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toList());
    }

    public static void loadClass(Class<?> clazz) {
        if(clazz == null) {
            throw new UnsupportedOperationException("Class is null.");
        }
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
