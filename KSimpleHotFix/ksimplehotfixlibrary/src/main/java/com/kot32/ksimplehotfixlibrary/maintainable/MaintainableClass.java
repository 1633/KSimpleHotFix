package com.kot32.ksimplehotfixlibrary.maintainable;

import com.kot32.ksimplehotfixlibrary.maintainable.i.Maintainable;
import com.kot32.ksimplehotfixlibrary.manager.FixManager;

/**
 * Created by kot32 on 16/3/6.
 */
public class MaintainableClass implements Maintainable {

    @Override
    public Maintainable getLatestClassInstance() {
        return getLatestClassInstance(new Class[]{}, new Object[]{});
    }

    @Override
    public Maintainable getLatestClassInstance(Class[] constructParameterTypes,
                                               Object[] constructParams) {
        Maintainable maintainable = (Maintainable) FixManager.getInstance().getLatestClassInstance(getClass(), null, constructParameterTypes, constructParams);
        if (maintainable == null) {
            return this;
        }
        return maintainable;
    }
}
