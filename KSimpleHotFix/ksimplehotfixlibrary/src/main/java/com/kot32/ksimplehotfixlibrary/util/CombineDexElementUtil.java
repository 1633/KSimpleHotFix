package com.kot32.ksimplehotfixlibrary.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by kot32 on 16/3/9.
 */
public class CombineDexElementUtil {

    public static void inject(PathClassLoader pathClassLoader, DexClassLoader loader) {

        try {
            Object dexElements = combineArray(
                    getDexElements(getPathList(pathClassLoader)),
                    getDexElements(getPathList(loader)));
            Object pathList = getPathList(pathClassLoader);
            setField(pathList, pathList.getClass(), "dexElements", dexElements);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        ClassLoader bc = (ClassLoader) baseDexClassLoader;
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    private static Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }

    private static void setField(Object obj, Class<?> cl, String field,
                                 Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
}
