package io.xdag.xdagwallet.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.NoTransactionException;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.Detail2AddressListFunction;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.util.UpdateUtil;
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

    // update
    @BindView(R.id.version_layout)
    LinearLayout mVersionLayout;
    @BindView(R.id.version_desc)
    TextView mTvVersionDesc;
    @BindView(R.id.version_update)
    TextView mTvVersionUpdate;
    @BindView(R.id.version_close)
    TextView mTvVersionClose;

    private TransactionAdapter mAdapter;
    private View mEmptyView;
    private CompositeDisposable mDisposable = new CompositeDisposable();

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
            mEmptyView.setOnClickListener(v -> requestTransaction());
        }

        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(null);
            mAdapter.setEmptyView(mEmptyView);
        }

        mRecyclerView.setAdapter(mAdapter);
        XdagEventManager.getInstance(getMainActivity()).addOnEventUpdateCallback(new XdagEventManager.OnEventUpdateCallback() {
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

    @Override
    protected void initData() {
        super.initData();
        requestUpdate();
    }


    private void requestUpdate() {
        mDisposable.add(ApiServer.getApi().getVersionInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(versionModel -> {
                    MLog.i(versionModel);
                    UpdateUtil.update(versionModel, mVersionLayout, mTvVersionDesc, mTvVersionUpdate, mTvVersionClose);
                }, new ErrorConsumer(mContext)));
    }


    private void requestTransaction() {

        mDisposable.add(ApiServer.getXdagScanApi().getBlockDetail(mTvAddress.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Detail2AddressListFunction())
                .subscribe(this::showTransaction, throwable -> {
                    // no transaction
                    if (throwable instanceof NoTransactionException) {
                        AlertUtil.show(mContext, throwable.getMessage());
                        return;
                    }

                    // if failed request api2 again
                    mDisposable.add(
                            ApiServer.getXdagScanApi2().getBlockDetail(mTvAddress.getText().toString())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map(new Detail2AddressListFunction())
                                    .subscribe(this::showTransaction, new ErrorConsumer(mContext))
                    );
                }));
    }


    private void showTransaction(List<BlockDetailModel.BlockAsAddress> blockAsAddresses) {
        mAdapter.setNewData(blockAsAddresses);
        AlertUtil.show(mContext, R.string.success_refresh);
    }


    public void showNotReady() {
        mTvAddress.setText(R.string.not_ready);
        mCollapsingToolbarLayout.setTitle(getString(R.string.not_ready));
        mAdapter.setNewData(null);
    }

    @OnClick(R.id.home_tv_address)
    void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override
    public void onDestroy() {
        RxUtil.dispose(mDisposable);
        super.onDestroy();
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        requestTransaction();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public int getPosition() {
        return 0;
    }
}
