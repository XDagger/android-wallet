package io.xdag.xdagwallet.util;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * created by lxm on 2018/7/19.
 */
public class RxUtil {

    public static void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public static void dispose(List<Disposable> disposableList) {
        for (Disposable disposable : disposableList) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }
}
