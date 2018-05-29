package io.xdag.xdagwallet.fragment;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import io.xdag.common.base.RefreshFragment;
import io.xdag.common.base.ToolbarActivity;
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
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private double mBalance = 10792.7;


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
        mCollapsingToolbarLayout.setTitle(String.format("%s XDAG", mBalance));
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((ToolbarActivity) mContext).mToolbar.setVisibility(View.GONE);
        }
    }


    @Override public void onRefresh() {
        super.onRefresh();
    }
}
