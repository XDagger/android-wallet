package io.xdag.common.base;

import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import io.xdag.common.tool.RefreshDelegate;

/**
 * created by ssyijiu  on 2018/7/29
 */
public abstract class RefreshActivity extends ToolbarActivity implements RefreshDelegate.OnRefreshListener {

    protected RefreshDelegate mRefreshDelegate;

    @Override
    public void setContentView(View view) {
        mRefreshDelegate = new RefreshDelegate(mContext, this);
        super.setContentView(mRefreshDelegate.getRootView());
        mRefreshDelegate.getContent().addView(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRefreshDelegate.setRefreshEnabled(isRefresh());
        ButterKnife.bind(this, view);
    }

    protected boolean isRefresh() {
        return true;
    }

    @Override
    public void onRefresh() {
    }
}
