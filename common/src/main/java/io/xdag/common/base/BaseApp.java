package io.xdag.common.base;

import android.app.Application;

import io.xdag.common.Common;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Common.init(this);
    }
}
