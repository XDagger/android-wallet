package io.xdag.common.tool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

/**
 * created by lxm on 2018/7/18.
 */
public abstract class NoLeakHandler<T> extends Handler {

    private final WeakReference<T> mTargetRef;


    public NoLeakHandler(T target, Looper looper) {
        super(looper);
        mTargetRef = new WeakReference<>(target);
    }


    @Override
    public final void handleMessage(Message msg) {
        T mTarget = mTargetRef.get();
        if (mTarget == null) {
            removeCallbacksAndMessages(null);
            return;
        }
        onMessageExecute(mTarget, msg);
    }


    protected abstract void onMessageExecute(T target, Message msg);

}
