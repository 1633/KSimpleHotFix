package com.kot32.ksimplehotfixlibrary.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.kot32.ksimplehotfixlibrary.maintainable.exception.CantFindLatestClassInstanceException;
import com.kot32.ksimplehotfixlibrary.util.SimpleDownloader;
import com.kot32.ksimplehotfixlibrary.util.reflect.Reflect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by kot32 on 16/3/6.
 * 1.对于普通的Java 类，可以直接继承 MaintainableClass ，再调用 getLatestClass 得到最新的实例
 * 2.对于已经继承了其他类的Java类，，利用 FixManager.getLatestClassInstance(String className) 来得到最新实例
 * 3.对于Activity，什么也不用改
 */
public class FixManager {


    private final static String JAR_DIR = "jar_dir";

    private final static String DEX_OUT = "dex_out_put";

    private final static long JAR_MIN_SIZE = 100;

    private static String PATCH_PATH = "";

    private static FixManager mManager;

    private HashMap<String, Class> latestClasses;

    private FixConfig mConfig;

    private File jarDir;

    private Context mContext;

    private File jarFile;

    public static ClassLoader ORIGINAL_LOADER;
    public static ClassLoader CUSTOM_LOADER = null;


    public static FixManager getInstance() {
        if (mManager == null) {
            mManager = new FixManager();
        }
        return mManager;
    }

    public void init(FixConfig config, Context context) {

        this.mConfig = config;
        this.mContext = context;
        latestClasses = new HashMap<>();
        jarDir = new File(mContext.getFilesDir(), JAR_DIR);
        startFindLatestClasses();
        if (!jarDir.exists()) {
            jarDir.mkdir();
        }
        File dexOutPut = new File(mContext.getFilesDir() + "/" + DEX_OUT);
        if (!dexOutPut.exists()) {
            dexOutPut.mkdir();
        }
        PATCH_PATH = mContext.getFilesDir() + "/" + JAR_DIR + "/" + "patch.apk";

    }

    public Object getLatestClassInstance(Class sourceClass) {
        return getLatestClassInstance(sourceClass, new FindLatestClassInstanceCallBack() {
            @Override
            public void OnSuccess(Object o) {

            }

            @Override
            public void onFailed() {

            }
        }, new Class[]{}, new Object[]{});
    }

    public Object getLatestClassInstance(Class sourceClass, FindLatestClassInstanceCallBack classInstanceCallBack) {
        return getLatestClassInstance(sourceClass, classInstanceCallBack, new Class[]{}, new Object[]{});
    }

    public Object getLatestClassInstance(Class sourceClass,
                                         FindLatestClassInstanceCallBack classInstanceCallBack,
                                         Class[] constructParameterTypes,
                                         Object[] constructParams
    )
            throws CantFindLatestClassInstanceException {

        Object maintainable = null;
        Class realObjectClazz = null;
        Class clazz = latestClasses.get(sourceClass.getName());
        if (clazz == null) {
            try {
                realObjectClazz = getClass().getClassLoader().loadClass(sourceClass.getName());
                if (jarFile == null && realObjectClazz != null) {
                    return getInstance(realObjectClazz, constructParameterTypes, constructParams);
                }

                ClassLoader originClassLoader = getClass().getClassLoader();

                DexClassLoader classLoader = new DexClassLoader(jarFile.getAbsolutePath()
                        , mContext.getFilesDir() + "/" + DEX_OUT
                        , null
                        , originClassLoader);

//                CombineDexElementUtil.inject((PathClassLoader) ORIGINAL_LOADER, classLoader);
                String fakeClassPath = makeFakePath(sourceClass.getName());
                clazz = classLoader.loadClass(fakeClassPath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (clazz != null) {
            latestClasses.put(sourceClass.getName(), clazz);
            maintainable = getInstance(clazz, constructParameterTypes, constructParams);
            System.out.println(maintainable);

        }
        if (maintainable == null) {
            if (realObjectClazz != null) {
                return getInstance(realObjectClazz, constructParameterTypes, constructParams);
            }
            throw new CantFindLatestClassInstanceException("Can't find latestClassInstance!");
        }

        return maintainable;
    }

    private void initCLassLoader() {

        class MyClassLoader extends ClassLoader {
            public MyClassLoader(ClassLoader parent) {
                super(parent);
            }

            @Override
            public Class<?> loadClass(String className)
                    throws ClassNotFoundException {
                if (CUSTOM_LOADER != null) {
                    try {
                        Class<?> c = CUSTOM_LOADER.loadClass(className);
                        if (c != null)
                            return c;
                    } catch (ClassNotFoundException e) {
                    }
                }
                return super.loadClass(className);
            }
        }

        Context mBase = Reflect.onObject(mContext).get("mBase");
        Object mPackageInfo = Reflect.onObject(mBase).get("mPackageInfo");

        Reflect rf = Reflect.onObject(mPackageInfo);
        ClassLoader mClassLoader = rf.get("mClassLoader");
        ORIGINAL_LOADER = mClassLoader;
        MyClassLoader cl = new MyClassLoader(mClassLoader);
        rf.set("mClassLoader", cl);

        DexClassLoader classLoader = new DexClassLoader(PATCH_PATH
                , mContext.getFilesDir() + "/" + DEX_OUT
                , null
                , ORIGINAL_LOADER.getParent());


        CUSTOM_LOADER = classLoader;
    }


    private Object getInstance(Class clazz, Class[] constructParameterTypes,
                               Object[] constructParams) {
        Object o = null;
        Constructor<?> localConstructor = null;
        try {
            localConstructor = clazz.getConstructor(constructParameterTypes);
            o = localConstructor.newInstance(constructParams);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;

    }

    private void startFindLatestClasses() {
        new downloadJarTask().execute();
    }


    public static class FixConfig {
        public String serverPath;
        public HashMap<String, Object> serverParams;

        public FixConfig(@NonNull String serverPath, @NonNull HashMap<String, Object> serverParams) {
            this.serverPath = serverPath;
            this.serverParams = serverParams;
        }
    }

    public interface FindLatestClassInstanceCallBack {
        void OnSuccess(Object o);

        void onFailed();
    }

    private class downloadJarTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            SimpleDownloader downloader = new SimpleDownloader();
            File apk = new File(PATCH_PATH);

            byte[] data = downloader.download(mConfig.serverPath);
            if (data == null || data.length < 1) {
                if (apk.exists() && apk.length() >= JAR_MIN_SIZE) {
                    jarFile = apk;
                }
                return null;
            }
            try {
                synchronized (apk) {
                    FileOutputStream fos = new FileOutputStream(apk.getAbsolutePath());
                    fos.write(data);
                    fos.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            jarFile = apk;
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            initCLassLoader();
        }
    }


    private String makeFakePath(String p) {
        String t = p.substring(0, p.lastIndexOf('.'));
        String pr = p.substring(p.lastIndexOf('.'), p.length());

        return t + ".patch" + pr;

    }

    public void cancelFix() {
        File f = new File(PATCH_PATH);
        if (f.exists()) {
            f.delete();
        }
    }

}
