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
import io.xdag.common.base.RefreshFragment;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.common.util.DialogUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    private String mAddress = "ewrXrSDbCmqH/fkLuQkEMiwed3709C2k";
    private static final String TAG = "XdagWallet";
    private Handler mXdagMessageHandler;


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
            }
            break;
        }
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
