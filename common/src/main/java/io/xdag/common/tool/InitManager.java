package io.xdag.common.tool;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import io.xdag.common.BuildConfig;
import io.xdag.common.Common;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class InitManager {

    private Application mApplication;


    private InitManager() {
    }


    private static class Lazy {
        private static final InitManager INSTANCE = new InitManager();
    }


    public static InitManager getInstance() {
        return Lazy.INSTANCE;
    }


    public void init(Application application) {
        mApplication = application;
        Common.init(mApplication, true);
        initLeakCanary();
    }


    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(mApplication)) {
            return;
        }
        LeakCanary.install(mApplication);
    }
}
