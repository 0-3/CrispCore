package network.reborn.core.Util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSUtils {
    private static String version = getVersion();
    private static Class<?> c = getOBCClass("block.CraftBlock");
    private static Method m;

    static {
        m = getMethod(c, "getNMSBlock");
    }

    public NMSUtils() {
    }

    public static String getVersion() {
        if (version != null) {
            return version;
        } else {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            return name.substring(name.lastIndexOf(46) + 1) + ".";
        }
    }

    public static Class<?> getNMSClassWithException(String className) throws Exception {
        return Class.forName("net.minecraft.server." + getVersion() + className);
    }

    public static Class<?> getNMSClass(String className) {
        try {
            return getNMSClassWithException(className);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Class<?> getNMSClassSilent(String className) {
        try {
            return getNMSClassWithException(className);
        } catch (Exception var2) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String className, String embedded) {
        try {
            return getNMSClassWithException(className);
        } catch (Exception var3) {
            return getInnerClassSilent(getNMSClassSilent(embedded), className);
        }
    }

    public static Class<?> getNMSClassSilent(String className, String embedded) {
        try {
            return getNMSClassWithException(className);
        } catch (Exception var3) {
            return getInnerClassSilent(getNMSClassSilent(embedded), className);
        }
    }

    public static Class<?> getOBCClassWithException(String className) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + getVersion() + className);
    }

    public static Class<?> getOBCClass(String className) {
        try {
            return getOBCClassWithException(className);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Class<?> getOBCClassSilent(String className) {
        try {
            return getOBCClassWithException(className);
        } catch (Exception var2) {
            return null;
        }
    }

    public static Object getHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Object getHandleSilent(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj);
        } catch (Exception var2) {
            return null;
        }
    }

    public static Object getBlockHandleWithException(Object obj) throws Exception {
        return m.invoke(obj);
    }

    public static Object getBlockHandle(Object obj) {
        try {
            return m.invoke(obj);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Object getBlockHandleSilent(Object obj) {
        try {
            return m.invoke(obj);
        } catch (Exception var2) {
            return null;
        }
    }

    public static Field getFieldWithException(Class<?> clazz, String name) throws Exception {
        Field[] var5;
        int var4 = (var5 = clazz.getDeclaredFields()).length;

        Field field;
        int var3;
        Field modifiersField;
        int modifiers;
        for (var3 = 0; var3 < var4; ++var3) {
            field = var5[var3];
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiers = modifiersField.getInt(field);
                modifiers &= -17;
                modifiersField.setInt(field, modifiers);
                return field;
            }
        }

        var4 = (var5 = clazz.getFields()).length;

        for (var3 = 0; var3 < var4; ++var3) {
            field = var5[var3];
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiers = modifiersField.getInt(field);
                modifiers &= -17;
                modifiersField.setInt(field, modifiers);
                return field;
            }
        }

        throw new Exception("Field Not Found");
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            return getFieldWithException(clazz, name);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Field getFieldSilent(Class<?> clazz, String name) {
        try {
            return getFieldWithException(clazz, name);
        } catch (Exception var3) {
            return null;
        }
    }

    public static Field getFieldOfTypeWithException(Class<?> clazz, Class<?> type, String name) throws Exception {
        Field[] var6;
        int var5 = (var6 = clazz.getDeclaredFields()).length;

        Field field;
        int var4;
        Field modifiersField;
        int modifiers;
        for (var4 = 0; var4 < var5; ++var4) {
            field = var6[var4];
            if (field.getName().equals(name) && field.getType().equals(type)) {
                field.setAccessible(true);
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiers = modifiersField.getInt(field);
                modifiers &= -17;
                modifiersField.setInt(field, modifiers);
                return field;
            }
        }

        var5 = (var6 = clazz.getFields()).length;

        for (var4 = 0; var4 < var5; ++var4) {
            field = var6[var4];
            if (field.getName().equals(name) && field.getType().equals(type)) {
                field.setAccessible(true);
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiers = modifiersField.getInt(field);
                modifiers &= -17;
                modifiersField.setInt(field, modifiers);
                return field;
            }
        }

        throw new Exception("Field Not Found");
    }

    public static Field getFieldOfType(Class<?> clazz, Class<?> type, String name) {
        try {
            return getFieldOfTypeWithException(clazz, type, name);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Field getFirstFieldOfTypeWithException(Class<?> clazz, Class<?> type) throws Exception {
        Field[] var5;
        int var4 = (var5 = clazz.getDeclaredFields()).length;

        for (int var3 = 0; var3 < var4; ++var3) {
            Field field = var5[var3];
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                int modifiers = modifiersField.getInt(field);
                modifiers &= -17;
                modifiersField.setInt(field, modifiers);
                return field;
            }
        }

        throw new Exception("Field Not Found");
    }

    public static Field getFirstFieldOfType(Class<?> clazz, Class<?> type) {
        try {
            return getFirstFieldOfTypeWithException(clazz, type);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Field getLastFieldOfTypeWithException(Class<?> clazz, Class<?> type) throws Exception {
        Field field = null;
        Field[] var6;
        int var5 = (var6 = clazz.getDeclaredFields()).length;

        Field modifiersField;
        int modifiers;
        for (modifiers = 0; modifiers < var5; ++modifiers) {
            modifiersField = var6[modifiers];
            if (modifiersField.getType().equals(type)) {
                field = modifiersField;
            }
        }

        if (field == null) {
            throw new Exception("Field Not Found");
        } else {
            field.setAccessible(true);
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiers = modifiersField.getInt(field);
            modifiers &= -17;
            modifiersField.setInt(field, modifiers);
            return field;
        }
    }

    public static Field getLastFieldOfType(Class<?> clazz, Class<?> type) {
        try {
            return getLastFieldOfTypeWithException(clazz, type);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class... args) {
        Method[] var6;
        int var5 = (var6 = clazz.getDeclaredMethods()).length;

        Method m;
        int var4;
        for (var4 = 0; var4 < var5; ++var4) {
            m = var6[var4];
            if (m.getName().equals(name) && (args.length == 0 && m.getParameterTypes().length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }

        var5 = (var6 = clazz.getMethods()).length;

        for (var4 = 0; var4 < var5; ++var4) {
            m = var6[var4];
            if (m.getName().equals(name) && (args.length == 0 && m.getParameterTypes().length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
                m.setAccessible(true);
                return m;
            }
        }

        return null;
    }

    public static Method getMethodSilent(Class<?> clazz, String name, Class... args) {
        try {
            return getMethod(clazz, name, args);
        } catch (Exception var4) {
            return null;
        }
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        if (l1.length != l2.length) {
            return false;
        } else {
            for (int i = 0; i < l1.length; ++i) {
                if (l1[i] != l2[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    public static Class<?> getInnerClassWithException(Class<?> c, String className) throws Exception {
        Class[] var5;
        int var4 = (var5 = c.getDeclaredClasses()).length;

        for (int var3 = 0; var3 < var4; ++var3) {
            Class cl = var5[var3];
            if (cl.getSimpleName().equals(className)) {
                return cl;
            }
        }

        return null;
    }

    public static Class<?> getInnerClass(Class<?> c, String className) {
        try {
            return getInnerClassWithException(c, className);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Class<?> getInnerClassSilent(Class<?> c, String className) {
        try {
            return getInnerClassWithException(c, className);
        } catch (Exception var3) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class... args) {
        Constructor[] var5;
        int var4 = (var5 = clazz.getDeclaredConstructors()).length;

        Constructor c;
        int var3;
        for (var3 = 0; var3 < var4; ++var3) {
            c = var5[var3];
            if (args.length == 0 && c.getParameterTypes().length == 0 || ClassListEqual(args, c.getParameterTypes())) {
                c.setAccessible(true);
                return c;
            }
        }

        var4 = (var5 = clazz.getConstructors()).length;

        for (var3 = 0; var3 < var4; ++var3) {
            c = var5[var3];
            if (args.length == 0 && c.getParameterTypes().length == 0 || ClassListEqual(args, c.getParameterTypes())) {
                c.setAccessible(true);
                return c;
            }
        }

        return null;
    }

    public static Constructor<?> getConstructorSilent(Class<?> clazz, Class... args) {
        try {
            return getConstructor(clazz, args);
        } catch (Exception var3) {
            return null;
        }
    }
}
