package io.xdag.common.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.ButterKnife;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Activity mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = View.inflate(mContext, getLayoutResId(), null);
        setContentView(rootView);
        initView(rootView);
    }


    protected abstract int getLayoutResId();

    protected abstract void initView(View rootView);


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        // butterKnife , if ToolbarActivity bind contentView in it
        if (!(this instanceof ToolbarActivity)) {
            ButterKnife.bind(this);
        }
    }
}
