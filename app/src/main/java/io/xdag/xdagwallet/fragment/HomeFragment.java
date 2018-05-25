package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import io.xdag.common.base.RefreshFragment;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class HomeFragment extends RefreshFragment {

    @BindView(R.id.home_rv)
    RecyclerView mRecyclerView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new TransactionAdapter());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((ToolbarActivity) mContext).mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        ToastUtil.show("refresh");
        completeRefresh();
    }
}
