package io.xdag.xdagwallet.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/24.
 */
public class EmptyView extends FrameLayout {
    public EmptyView(@NonNull Context context) {
        this(context, null);
    }


    public EmptyView(
        @NonNull Context context,
        @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    public EmptyView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        View.inflate(context, R.layout.view_empty, this);
    }
}
