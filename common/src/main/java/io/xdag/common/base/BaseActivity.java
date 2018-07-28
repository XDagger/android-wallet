package io.xdag.common.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

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
        initView(rootView, savedInstanceState);
        if (getIntent() != null) {
            parseIntent(getIntent());
        }
        if (enableEventBus()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
        initData();
    }


    protected abstract int getLayoutResId();

    protected abstract void initView(View rootView, Bundle savedInstanceState);


    protected void parseIntent(Intent intent) {
    }


    protected void initData() {
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        // butterKnife , if ToolbarActivity bind contentView in it
        if (!(this instanceof ToolbarActivity)) {
            ButterKnife.bind(this);
        }
    }


    protected void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                         @NonNull Fragment fragment, int frameId, String tag) {
        Objects.requireNonNull(fragmentManager);
        Objects.requireNonNull(fragment);
        fragmentManager.beginTransaction()
                .add(frameId, fragment, tag)
                .commit();
    }

    @Override
    protected void onDestroy() {
        if (enableEventBus()) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
        super.onDestroy();
    }

    /**
     * default enable EventBus
     */
    protected boolean enableEventBus() {
        return false;
    }
}
