package io.xdag.xdagwallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * created by lxm on 2018/7/26.
 */
public class TranDetailHeaderView extends FrameLayout {

    public TranDetailHeaderView(@NonNull Context context) {
        this(context, null);
    }


    public TranDetailHeaderView(
        @NonNull Context context,
        @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public TranDetailHeaderView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    public TranDetailHeaderView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
    }
}
