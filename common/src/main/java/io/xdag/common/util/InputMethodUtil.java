package io.xdag.common.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import io.xdag.common.Common;

public class InputMethodUtil {

    private InputMethodUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("InputMethods cannot be instantiated !");
    }


    private static Handler sHandler = new Handler(Looper.getMainLooper());


    private static InputMethodManager getInputMethodManager() {
        return (InputMethodManager) Common.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    public static void showSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            showSoftInput(view);
        }
    }


    public static void showSoftInput(final View view) {
        sHandler.post(new Runnable() {
            @Override public void run() {
                getInputMethodManager().showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        });

    }


    public static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideSoftInput(view);
        }
    }


    public static void hideSoftInput(final View view) {
        sHandler.post(new Runnable() {
            @Override public void run() {
                getInputMethodManager().hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

    }


    public static boolean isActive() {
        return getInputMethodManager().isActive();
    }
}
