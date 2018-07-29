package io.xdag.xdagwallet.wrapper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import io.xdag.common.Common;
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.dialog.InputBuilder;
import io.xdag.xdagwallet.dialog.LoadingBuilder;
import io.xdag.xdagwallet.dialog.TipBuilder;
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
                MLog.i("Event: set password and random");
                mLoadingDialog.dismiss();
                mInputDialog.setMessage(getAuthHintString(event.eventType));
                mInputDialog.show();
            }
            break;
            case XdagEvent.en_event_pwd_not_same: {
                MLog.i("Event: password not same");
                mLoadingDialog.dismiss();
                mTipDialog.setMessage(Common.getString(R.string.error_password_not_same));
                mTipDialog.show();
            }
            break;
            case XdagEvent.en_event_pwd_error: {
                MLog.i("Event: password error");
                mLoadingDialog.dismiss();
                mTipDialog.setMessage(Common.getString(R.string.error_password));
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
                MLog.i("last  program state is " + mLastProgramState);
                MLog.i("event program state is " + event.programState);
                if (event.programState == XdagEvent.POOL && mLastProgramState == XdagEvent.XFER) {
                    notifyEventXfer(event);
                }
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
                if(callback != null) {
                    callback.onEventUpdate(event);
                }
            }
        }
    }

    private void notifyAddressReady(XdagEvent event) {
        if (!mEventUpdateCallbacks.isEmpty()) {
            for (OnEventUpdateCallback callback : mEventUpdateCallbacks) {
                if(callback != null) {
                    callback.onAddressReady(event);
                }
            }
        }
    }

    private void notifyEventXfer(XdagEvent event) {
        if (!mEventUpdateCallbacks.isEmpty()) {
            for (OnEventUpdateCallback callback : mEventUpdateCallbacks) {
                if(callback != null) {
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
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XdagWrapper.getInstance().XdagNotifyMsg("");
                        dialog.dismiss();
                        mLoadingBuilder.setMessage(R.string.please_wait_read_wallet);
                        mLoadingDialog.show();

                    }
                }).create();

        mInputDialog = new InputBuilder(mActivity)
                .setPositiveListener(new InputBuilder.OnPositiveClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, String input) {
                        if (input.length() < 6) {
                            AlertUtil.show(mActivity, R.string.error_password_format);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
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


    private String getAuthHintString(int eventType) {
        switch (eventType) {
            case XdagEvent.en_event_type_pwd:
                return Common.getString(R.string.please_input_password);
            case XdagEvent.en_event_set_pwd:
                return Common.getString(R.string.please_set_password);
            case XdagEvent.en_event_retype_pwd:
                return Common.getString(R.string.please_retype_password);
            case XdagEvent.en_event_set_rdm:
                return Common.getString(R.string.please_input_random);
            default:
                return Common.getString(R.string.please_input_password);
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