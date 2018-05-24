package io.xdag.common.base;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import io.xdag.common.Common;
import io.xdag.common.tools.InitManager;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitManager.getInstance().init(this);
    }
}
