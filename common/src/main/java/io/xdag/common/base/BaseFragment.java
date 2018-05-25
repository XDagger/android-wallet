package io.xdag.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc :
 */

public abstract class BaseFragment extends Fragment {

    protected Activity mContext;
    protected Unbinder mUnbinder;
    private boolean mFirstShow = true;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResId(), container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        initView(view);
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // for lazy init data
        if (!hidden && mFirstShow) {
            mFirstShow = false;
            initData();
        }
    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroyView();
    }


    protected abstract int getLayoutResId();

    protected abstract void initView(View rootView);

    protected void initData() {
    }
}
