package io.xdag.xdagwallet.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.RefreshFragment;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.widget.AlertWrap;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class HomeFragment extends RefreshFragment {

    @BindView(R.id.home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.home_tv_address)
    TextView mTvAddress;

    private double mBalance = 10792.7;
    private String mAddress = "ewrXrSDbCmqH/fkLuQkEMiwed3709C2k";


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
        mTvAddress.setText(mAddress);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new TransactionAdapter());
        mCollapsingToolbarLayout.setTitle(String.format("%s XDAG", mBalance));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangedListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                switch (state) {
                    case EXPANDED:
                        getRefreshDelegate().setRefreshEnabled(true);
                        break;
                    default:
                        getRefreshDelegate().setRefreshEnabled(false);
                }
            }
        });
    }


    @OnClick(R.id.home_tv_address) void copyAddress() {
        ((MainActivity) mContext).copyText(mTvAddress.getText().toString());
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getToolbar().setVisibility(View.GONE);
        }
    }


    @Override public void onRefresh() {
        super.onRefresh();
        AlertWrap.show(mContext, getString(R.string.refresh_success));
    }
}
