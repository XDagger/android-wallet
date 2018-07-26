package io.xdag.xdagwallet.fragment;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
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
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.Detail2AddressListFunction;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.dialog.InputBuilder;
import io.xdag.xdagwallet.dialog.LoadingBuilder;
import io.xdag.xdagwallet.dialog.TipBuilder;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.widget.EmptyView;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;

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

    private int mLastAddressState = XdagEvent.en_address_not_ready;
    private int mLastBalancState = XdagEvent.en_balance_not_ready;
    private TransactionAdapter mAdapter;
    private View mEmptyView;
    private Disposable mDisposable;
    private LoadingBuilder mLoadingBuilder;
    private AlertDialog mLoadingDialog;
    private AlertDialog mTipDialog;
    private AlertDialog mInputDialog;

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
        initDialog();

    }


    private void initDialog() {
        mLoadingBuilder = new LoadingBuilder(mContext)
            .setMessage(R.string.please_wait_read_wallet);
        mLoadingDialog = mLoadingBuilder.create();

        mTipDialog = new TipBuilder(mContext)
            .setPositiveListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    XdagWrapper.getInstance().XdagNotifyMsg("");
                    dialog.dismiss();
                    mLoadingBuilder.setMessage(R.string.please_wait_read_wallet);
                    mLoadingDialog.show();

                }
            }).create();

        mInputDialog = new InputBuilder(mContext)
            .setPositiveListener(new InputBuilder.OnPositiveClickListener() {
                @Override
                public void onClick(DialogInterface dialog, String input) {
                    if (input.length() < 6) {
                        AlertUtil.show(mContext, R.string.error_password_format);
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                mInputDialog.show();
                            }
                        }, 500);
                    } else {
                        XdagWrapper.getInstance().XdagNotifyMsg(input);
                        dialog.dismiss();
                        mLoadingBuilder.setMessage(R.string.please_wait_connecting_pool);
                        mLoadingDialog.show();
                    }
                }
            }).create();

        mLoadingDialog.show();
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
        switch (event.eventType) {
            case XdagEvent.en_event_type_pwd:
            case XdagEvent.en_event_set_pwd:
            case XdagEvent.en_event_retype_pwd:
            case XdagEvent.en_event_set_rdm: {
                MLog.i("Event: set password and random");
                if (isVisible()) {
                    mLoadingDialog.dismiss();
                    mInputDialog.setMessage(getAuthHintString(event.eventType));
                    mInputDialog.show();
                }
            }
            break;
            case XdagEvent.en_event_pwd_not_same: {
                MLog.i("Event: password not same");
                if (isVisible()) {
                    mLoadingDialog.dismiss();
                    mTipDialog.setMessage(getString(R.string.error_password_not_same));
                    mTipDialog.show();
                }
            }
            break;
            case XdagEvent.en_event_pwd_error: {
                MLog.i("Event: password error");
                if (isVisible()) {
                    mLoadingDialog.dismiss();
                    mTipDialog.setMessage(getString(R.string.error_password));
                    mTipDialog.show();
                }
            }
            break;
            case XdagEvent.en_event_update_state: {
                MLog.i("Event: state update");
                mTvAddress.setText(event.address);
                if (isVisible()) {
                    mCollapsingToolbarLayout.setTitle(event.balance);
                    if (mLastAddressState == XdagEvent.en_address_not_ready &&
                        event.addressLoadState == XdagEvent.en_address_ready) {
                        requestTransaction();
                    }

                    if (getXdagHandler().isNotConnectedToPool(event)) {
                        if (!mInputDialog.isShowing()) {
                            mLoadingBuilder.setMessage(
                                getString(R.string.please_wait_connecting_pool));
                            mLoadingDialog.show();
                        }
                    } else {
                        mLoadingDialog.dismiss();
                    }
                }
            }
            break;
            default:
        }

        // update  address load state and balance  load state
        mLastAddressState = event.addressLoadState;
        mLastBalancState = event.balanceLoadState;
    }


    private String getAuthHintString(int eventType) {
        switch (eventType) {
            case XdagEvent.en_event_type_pwd:
                return getString(R.string.please_input_password);
            case XdagEvent.en_event_set_pwd:
                return getString(R.string.please_set_password);
            case XdagEvent.en_event_retype_pwd:
                return getString(R.string.please_retype_password);
            case XdagEvent.en_event_set_rdm:
                return getString(R.string.please_input_random);
            default:
                return getString(R.string.please_input_password);
        }
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public int getPosition() {
        return 0;
    }
}
