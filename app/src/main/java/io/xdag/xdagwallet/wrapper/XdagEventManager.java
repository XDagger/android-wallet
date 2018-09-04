package io.xdag.xdagwallet.wrapper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;

import io.xdag.common.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;

import io.xdag.common.Common;
import io.xdag.common.tool.MLog;
import io.xdag.common.util.InputMethodUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.dialog.InputBuilder;
import io.xdag.xdagwallet.dialog.LoadingBuilder;
import io.xdag.xdagwallet.dialog.TipBuilder;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by ssyijiu  on 2018/7/28
 * <p>
 * manage the xdag event from c
 */
public class XdagEventManager {

    private MainActivity mActivity;
    private int mLastAddressState = XdagEvent.en_address_not_ready;
    private int mLastBalancState = XdagEvent.en_balance_not_ready;
    private int mLastProgramState = XdagEvent.NINT;

    private List<OnEventUpdateCallback> mEventUpdateCallbacks = new ArrayList<>();

    private LoadingBuilder mLoadingBuilder;
    private AlertDialog mLoadingDialog;
    private AlertDialog mTipDialog;
    private AlertDialog mInputDialog;

    @SuppressLint("StaticFieldLeak")
    private static XdagEventManager sInstance = null;


    public static XdagEventManager getInstance(MainActivity activity) {
        synchronized (XdagHandlerWrapper.class) {
            if (sInstance == null) {
                synchronized (XdagHandlerWrapper.class) {
                    sInstance = new XdagEventManager(activity);
                }
            }
        }
        return sInstance;
    }


    private XdagEventManager(MainActivity activity) {
        mActivity = activity;
    }


    public void manageEvent(XdagEvent event) {
        switch (event.eventType) {
            case XdagEvent.en_event_type_pwd:
            case XdagEvent.en_event_set_pwd:
            case XdagEvent.en_event_retype_pwd:
            case XdagEvent.en_event_set_rdm: {
                mLoadingDialog.dismiss();
                mInputDialog.setMessage(getTipMessage(event.eventType));
                mInputDialog.show();
                showInputNegativeButton(event);
                InputMethodUtil.showSoftInput(mActivity);
            }
            break;
            case XdagEvent.en_event_pwd_not_same:
            case XdagEvent.en_event_pwd_error:
            case XdagEvent.en_event_nothing_transfer:
            case XdagEvent.en_event_balance_too_small:
            case XdagEvent.en_event_invalid_recv_address: {
                mLoadingDialog.dismiss();
                mTipDialog.setMessage(getTipMessage(event.eventType));
                mTipDialog.show();
            }
            break;
            case XdagEvent.en_event_update_state: {
                MLog.i("Event: state update");
                notifyEventUpdate(event);
                if (mLastAddressState == XdagEvent.en_address_not_ready &&
                    event.addressLoadState == XdagEvent.en_address_ready) {
                    notifyAddressReady(event);
                }

                if (mActivity.getXdagHandler().isNotConnectedToPool(event)) {
                    if (!mInputDialog.isShowing()) {
                        mLoadingBuilder.setMessage(Common.getString(R.string.please_wait_connecting_pool));
                        mLoadingDialog.show();
                    }
                } else {
                    mLoadingDialog.dismiss();
                }

                // xfer success
                if (event.programState == XdagEvent.POOL && mLastProgramState == XdagEvent.XFER) {
                    notifyEventXfer(event);
                }

                // sending coin
                if(event.programState == XdagEvent.XFER && mLastProgramState == XdagEvent.POOL) {
                    AlertUtil.show(mActivity,R.string.success_send_coin);
                }
            }
            break;
            case XdagEvent.en_event_disconneted_finished: {
                MLog.i("disconnected from pool finished reconnected to the pool");
                mLoadingBuilder.setMessage(R.string.please_wait_read_wallet);
                mLoadingDialog.show();
                XdagHandlerWrapper.getInstance(mActivity).connectToPool(Config.getPoolAddress());
            }
            break;
            default:
        }

        // update  address load state and balance  load state
        mLastAddressState = event.addressLoadState;
        mLastBalancState = event.balanceLoadState;
        // update program state
        mLastProgramState = event.programState;
    }


    private void notifyEventUpdate(XdagEvent event) {
        if (!mEventUpdateCallbacks.isEmpty()) {
            for (OnEventUpdateCallback callback : mEventUpdateCallbacks) {
                if (callback != null) {
                    callback.onEventUpdate(event);
                }
            }
        }
    }


    private void notifyAddressReady(XdagEvent event) {
        if (!mEventUpdateCallbacks.isEmpty()) {
            for (OnEventUpdateCallback callback : mEventUpdateCallbacks) {
                if (callback != null) {
                    callback.onAddressReady(event);
                }
            }
        }
    }


    private void notifyEventXfer(XdagEvent event) {
        if (!mEventUpdateCallbacks.isEmpty()) {
            for (OnEventUpdateCallback callback : mEventUpdateCallbacks) {
                if (callback != null) {
                    callback.onEventXfer(event);
                }
            }
        }
    }


    public void initDialog() {
        mLoadingBuilder = new LoadingBuilder(mActivity)
            .setMessage(R.string.please_wait_read_wallet);
        mLoadingDialog = mLoadingBuilder.create();

        mTipDialog = new TipBuilder(mActivity)
            .setPositiveListener((dialog, which) -> {
                XdagWrapper.getInstance().XdagNotifyMsg();
                dialog.dismiss();
                mLoadingBuilder.setMessage(R.string.please_wait_read_wallet);
                mLoadingDialog.show();

            }).create();

        mInputDialog = new InputBuilder(mActivity)
            .setPositiveListener((dialog, input) -> {
                if (input.length() < 6) {
                    AlertUtil.show(mActivity, R.string.error_password_format);
                    new Handler().postDelayed(() -> {
                        mInputDialog.show();
                        InputMethodUtil.showSoftInput(mActivity);
                    }, 500);
                } else {
                    XdagWrapper.getInstance().XdagNotifyMsg(input);
                    dialog.dismiss();
                    mLoadingBuilder.setMessage(R.string.please_wait_connecting_pool);
                    mLoadingDialog.show();
                }
                InputMethodUtil.hideSoftInput(mActivity);
            })
            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                XdagWrapper.getInstance().XdagNotifyMsg();
                InputMethodUtil.hideSoftInput(mActivity);
            }).create();

        mLoadingDialog.show();
    }


    private String getTipMessage(int eventType) {
        switch (eventType) {
            case XdagEvent.en_event_type_pwd:
                return Common.getString(R.string.please_input_password);
            case XdagEvent.en_event_set_pwd:
                return Common.getString(R.string.please_set_password);
            case XdagEvent.en_event_retype_pwd:
                return Common.getString(R.string.please_retype_password);
            case XdagEvent.en_event_set_rdm:
                return Common.getString(R.string.please_input_random);
            case XdagEvent.en_event_pwd_error:
                return Common.getString(R.string.error_password);
            case XdagEvent.en_event_pwd_not_same:
                return Common.getString(R.string.error_password_not_same);
            case XdagEvent.en_event_nothing_transfer:
                return Common.getString(R.string.error_noting_to_transfer);
            case XdagEvent.en_event_balance_too_small:
                return Common.getString(R.string.error_balance_too_small);
            case XdagEvent.en_event_invalid_recv_address:
                return Common.getString(R.string.error_invalid_recv_address);
            default:
                return Common.getString(R.string.please_input_password);
        }
    }


    private void showInputNegativeButton(XdagEvent event) {
        if (event.eventType == XdagEvent.en_event_type_pwd &&
            mActivity.mShowFragment instanceof SendFragment) {
            mInputDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        } else {
            mInputDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
        }
    }


    public void addOnEventUpdateCallback(OnEventUpdateCallback callback) {
        mEventUpdateCallbacks.add(callback);
    }


    /**
     * update UI when event update
     */
    public interface OnEventUpdateCallback {
        void onAddressReady(XdagEvent event);

        void onEventUpdate(XdagEvent event);

        void onEventXfer(XdagEvent event);
    }
}