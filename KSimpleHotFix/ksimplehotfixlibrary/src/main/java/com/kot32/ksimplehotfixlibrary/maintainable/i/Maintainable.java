package com.kot32.ksimplehotfixlibrary.maintainable.i;

/**
 * Created by kot32 on 16/3/6.
 */
public interface Maintainable {
    Maintainable getLatestClassInstance();

    Maintainable getLatestClassInstance(Class[] constructParameterTypes,
                                        Object[] constructParams);
}
