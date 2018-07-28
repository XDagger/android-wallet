package io.xdag.xdagwallet.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.Detail2AddressListFunction;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.widget.EmptyView;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagEventManager;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class HomeFragment extends BaseMainFragment {

    @BindView(R.id.home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.home_tv_address)
    TextView mTvAddress;

    private TransactionAdapter mAdapter;
    private View mEmptyView;
    private Disposable mDisposable;
    private XdagEventManager mXdagEventManager;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvAddress.setText(R.string.not_ready);
        mCollapsingToolbarLayout.setTitle(getString(R.string.not_ready));
        mRecyclerView.setHasFixedSize(true);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangedListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                getRefreshDelegate().setRefreshEnabled(
                        state.equals(AppBarStateChangedListener.State.EXPANDED));
            }
        });

        if (mEmptyView == null) {
            mEmptyView = new EmptyView(mContext);
            mEmptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestTransaction();
                }
            });
        }

        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(null);
            mAdapter.setEmptyView(mEmptyView);
        }

        mRecyclerView.setAdapter(mAdapter);
        mXdagEventManager = XdagEventManager.getInstance(getMainActivity());
        mXdagEventManager.initDialog();
        mXdagEventManager.addOnEventUpdateCallback(new XdagEventManager.OnEventUpdateCallback() {
            @Override
            public void onAddressReady(XdagEvent event) {
                requestTransaction();
            }

            @Override
            public void onEventUpdate(XdagEvent event) {
                mTvAddress.setText(event.address);
                mCollapsingToolbarLayout.setTitle(event.balance);
            }

            @Override
            public void onEventXfer(XdagEvent event) {
                requestTransaction();
            }
        });

    }


    private void requestTransaction() {

        mDisposable = ApiServer.getApi().getBlockDetail(mTvAddress.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Detail2AddressListFunction())
                .subscribe(new Consumer<List<BlockDetailModel.BlockAsAddress>>() {
                    @Override
                    public void accept(List<BlockDetailModel.BlockAsAddress> blockAsAddresses) {
                        mAdapter.setNewData(blockAsAddresses);
                        AlertUtil.show(mContext, R.string.success_refresh);
                    }
                }, new ErrorConsumer(getMainActivity()));
    }


    @OnClick(R.id.home_tv_address)
    void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        requestTransaction();
    }


    /**
     * the event from c
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        mXdagEventManager.manageEvent(event);
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public int getPosition() {
        return 0;
    }
}
