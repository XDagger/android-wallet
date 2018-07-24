package io.xdag.xdagwallet.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.common.tool.MLog;
import io.xdag.common.util.DialogUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.Detail2AddressListFunction;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.dialog.LoadingDialog;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.widget.EmptyView;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private LoadingDialog mLoadingDialog;


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
                @Override public void onClick(View v) {
                    requestTransaction();
                }
            });
        }

        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(null);
            mAdapter.setEmptyView(mEmptyView);
        }

        mRecyclerView.setAdapter(mAdapter);
        mLoadingDialog = LoadingDialog.newInstance("正在连接矿池，请稍后", true);

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


    @OnClick(R.id.home_tv_address) void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override public void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }


    @Override public void onRefresh() {
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
                // show dialog and ask user to type in password
                if (isVisible()) {
                    MLog.i("home fragment show the auth dialog");
                    // if (DialogUtil.isShow()) {
                    //     DialogUtil.dismissLoadingDialog();
                    // }
                    mLoadingDialog.dismiss();

                    DialogUtil.showAlertDialog(mContext, GetAuthHintString(event.eventType),
                        null, mContext.getString(R.string.alert_dialog_ok), null);
                    DialogUtil.getAlertDialog()
                        .setEditPwdMode(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    DialogUtil.getAlertDialog().setEditShow(true);
                    DialogUtil.setLeftListener(new DialogUtil.OnLeftListener() {
                        @Override
                        public void onClick() {
                            String authInfo = DialogUtil.getAlertDialog().getEditMessage();
                            XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                            xdagWrapper.XdagNotifyMsg(authInfo);
                        }
                    });
                }
            }
            break;
            case XdagEvent.en_event_pwd_error: {
                MLog.i("Event: password error");
                if (isVisible()) {
                    // DialogUtil.dismissLoadingDialog();
                    mLoadingDialog.dismiss();
                }
                //ask user to type password again
                DialogUtil.showAlertDialog(getActivity(), null, "password error", "OK", null);
                DialogUtil.setLeftListener(new DialogUtil.OnLeftListener() {
                    @Override
                    public void onClick() {
                        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                        xdagWrapper.XdagNotifyMsg("");
                    }
                });
            }
            break;
            case XdagEvent.en_event_update_state: {
                MLog.i("Event: state update");
                mTvAddress.setText(event.address);
                mCollapsingToolbarLayout.setTitle(event.balance);

                if (isVisible()) {
                    if (getXdagHandler().isNotConnectedToPool(event)) {
                        mLoadingDialog.show(getFragmentManager());
                    } else {
                        mLoadingDialog.dismiss();
                    }
                }

                // if (isVisible() && !DialogUtil.isShow()) {
                //     if (event.programState < XdagEvent.CONN) {
                //         DialogUtil.showLoadingDialog(getMainActivity(), "Loading......", false);
                //     } else {
                //         DialogUtil.dismissLoadingDialog();
                //     }
                // }

                if (mLastAddressState == XdagEvent.en_address_not_ready &&
                    event.addressLoadState == XdagEvent.en_address_ready) {
                    requestTransaction();
                }
            }
            break;
            default:
        }

        // update  address load state and balance  load state
        mLastAddressState = event.addressLoadState;
        mLastBalancState = event.balanceLoadState;
    }


    private String GetAuthHintString(int eventType) {
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
