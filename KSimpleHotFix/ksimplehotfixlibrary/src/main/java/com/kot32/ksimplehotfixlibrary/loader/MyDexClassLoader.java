package com.kot32.ksimplehotfixlibrary.loader;

/**
 * Created by kot32 on 16/3/7.
 */
public class MyDexClassLoader extends ClassLoader {

//    private DexFile mDexFile;
//
//    public MyDexClassLoader(DexFile dexFile){
//        mDexFile=dexFile;
//    }
//
//    @Override
//    protected Class<?> findClass(String className)
//            throws ClassNotFoundException {
//        Class<?> clazz = mDexFile.loadClass(className, this);
//        if (clazz==null) {
//            return Class.forName(className);
//        }
//        if (clazz == null) {
//            throw new ClassNotFoundException(className);
//        }
//        return clazz;
//    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

        return super.loadClass(className, resolve);
    }
}
