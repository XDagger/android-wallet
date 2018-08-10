package io.xdag.xdagwallet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/30.
 *
 * desc :
 */
public class MoreItemView extends FrameLayout {

    private TextView mTvText;


    public MoreItemView(@NonNull Context context) {
        this(context, null);
    }


    public MoreItemView(
        @NonNull Context context,
        @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MoreItemView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    public MoreItemView(
        @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        View rootView = View.inflate(context, R.layout.view_more_item, this);
        mTvText = rootView.findViewById(R.id.more_item_title);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MoreItemView);
        CharSequence text = a.getText(R.styleable.MoreItemView_android_text);
        a.recycle();
        mTvText.setText(text);

    }


    public void setText(int res) {
        mTvText.setText(res);
    }


    public void setText(CharSequence title) {
        mTvText.setText(title);
    }
}
