package io.xdag.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import io.xdag.common.tool.RefreshDelegate;

/**
 * created by lxm on 2018/5/25.
 * <p>
 * desc :
 */
public abstract class RefreshFragment extends BaseFragment
    implements RefreshDelegate.OnRefreshListener {

    private RefreshDelegate mRefreshDelegate;


    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRefreshDelegate = new RefreshDelegate(mContext, this);
        return mRefreshDelegate.getRootView();

    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mRefreshDelegate.setRefreshEnabled(isRefresh());
        FrameLayout content = mRefreshDelegate.getContent();
        View contentView = View.inflate(mContext, getLayoutResId(), null);
        content.addView(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        if (!(this instanceof ListFragment)) {
            mUnbinder = ButterKnife.bind(this, contentView);
        }

    }


    protected boolean isRefresh() {
        return true;
    }


    @Override
    public void onRefresh() {
    }

}
