package io.xdag.xdagwallet.fragment;

import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.xdag.common.base.RefreshFragment;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.common.util.DialogUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.XdagScanServer;
import io.xdag.xdagwallet.api.response.XdagScanBlockDetail;
import io.xdag.xdagwallet.api.response.XdagScanResp;
import io.xdag.xdagwallet.model.TransactionModel;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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


    private int mLastAddressState = XdagEvent.en_address_not_ready;
    private int mLastBalancState = XdagEvent.en_balance_not_ready;
    private String mAddress = "ewrXrSDbCmqH/fkLuQkEMiwed3709C2k";
    private static final String TAG = "XdagWallet";
    private Handler mXdagMessageHandler;
    private TransactionAdapter mTransactionAdapter = new TransactionAdapter();

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        EventBus.getDefault().register(homeFragment);
        return homeFragment;
    }


    public void setMessagehandler(Handler xdagMessageHandler) {
        this.mXdagMessageHandler = xdagMessageHandler;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();
        mRecyclerView.setAdapter(mTransactionAdapter);
    }

    private void loadHistory(){
        Log.i(TAG,"xdag server init  data start.....");
        XdagScanServer.getApi().getBlockDetail(mTvAddress.getText().toString(),0,20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<XdagScanResp<XdagScanBlockDetail>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG,"xdag server scan onSubscribe");
                    }

                    @Override
                    public void onNext(XdagScanResp<XdagScanBlockDetail> xdagScanBlockDetailXdagScanResp) {
                        Log.i(TAG,"xdag server scan onNext");
                        List<XdagScanBlockDetail.AddressList> addressList;
                        addressList = xdagScanBlockDetailXdagScanResp.data.address_list;

                        if(addressList == null || addressList.size() <= 0){
                            return;
                        }

                        List<TransactionModel> transactionModelList = new ArrayList<>();
                        for(int i = 0; i < addressList.size();i ++){
                            XdagScanBlockDetail.AddressList address = addressList.get(i);
                            TransactionModel model = new TransactionModel(address.address,address.amount,address.time,getDirecttoinType(address.direction));
                            transactionModelList.add(model);
                        }
                        mTransactionAdapter.setNewData(transactionModelList);
                        mTransactionAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG,"xdag server scan onError");

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG,"xdag server scan onComplete");
                    }
                });
    }

    private TransactionModel.Type getDirecttoinType(String direction){
        if(direction.equals("input")){
            return TransactionModel.Type.INPUT;
        }
        return TransactionModel.Type.OUTPUT;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvAddress.setText(R.string.not_ready);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new TransactionAdapter());
        mCollapsingToolbarLayout.setTitle(getString(R.string.not_ready));
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
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
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
        AlertUtil.show(mContext, getString(R.string.refresh_success));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        Log.i(TAG, "home fragment process msg in Thread " + Thread.currentThread().getId());
        Log.i(TAG, "event event type is " + event.eventType);
        String titleMsg = "";
        switch (event.eventType) {
            case XdagEvent.en_event_type_pwd:
            case XdagEvent.en_event_set_pwd:
            case XdagEvent.en_event_retype_pwd:
            case XdagEvent.en_event_set_rdm: {
                //show dialog and ask user to type in password
                if (isVisible()) {
                    Log.i(TAG, "home fragment show the auth dialog");
                    if (DialogUtil.isShow()) {
                        DialogUtil.dismissLoadingDialog();
                    }

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
                if (isVisible() && DialogUtil.isShow()) {
                    DialogUtil.dismissLoadingDialog();
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
                mCollapsingToolbarLayout.setTitle(event.balance);
                mTvAddress.setText(event.address);
                if (isVisible() && !DialogUtil.isShow()) {
                    if (event.programState < XdagEvent.CONN) {
                        DialogUtil.showLoadingDialog(getContext(), "Loading......", false);
                    } else {
                        DialogUtil.dismissLoadingDialog();
                    }
                }

                if(mLastAddressState == XdagEvent.en_address_not_ready &&
                        event.addressLoadState == XdagEvent.en_address_ready){
                    this.loadHistory();
                }
            }
            break;
        }

        //update  address load state and balance  load state
        this.mLastAddressState = event.addressLoadState;
        this.mLastBalancState = event.balanceLoadState;
    }


    private String GetAuthHintString(final int eventType) {
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
}
