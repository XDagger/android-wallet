package io.xdag.xdagwallet.fragment;

import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.QRManager;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;

import java.util.List;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,AuthDialogFragment.AuthInputListener {

    private Handler mXdagMessageHandler;
    private static final String TAG = "XdagWallet";

    public void setMessagehandler(Handler xdagMessageHandler){
        this.mXdagMessageHandler = xdagMessageHandler;
    }
    
    @BindView(R.id.send_et_amount) EditText mEtAmount;
    @BindView(R.id.send_et_address) EditText mEtAddress;
    @BindView(R.id.send_btn_xdag) Button mBtnSendXdag;
    @BindView(R.id.send_tv_available)TextView mTvAvailable;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        getToolbar().inflateMenu(R.menu.toolbar_scan);
        getToolbar().setOnMenuItemClickListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            AndPermission.with(mContext)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                        ZbarUtil.startScan(mContext, new QRManager.OnScanResultCallback() {
                            @Override
                            public void onScanSuccess(String result) {
                                mEtAddress.setText(result);
                            }

                            @Override
                            public void onScanFailed() {
                                AlertUtil.show(mContext, R.string.cannot_identify_qr_code);
                            }
                        });
                    }
                })
                .start();
        }
        return false;
    }

    @OnClick({ R.id.send_btn_xdag, R.id.send_et_address,R.id.send_et_amount }) void sendXdag() {
        String address = mEtAddress.getText().toString();
        String amount = mEtAmount.getText().toString();

        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
        xdagWrapper.XdagXferToAddress(address,amount);
    }

    public static SendFragment newInstance() {
        SendFragment fragment = new SendFragment();
        EventBus.getDefault().register(fragment);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        Log.i(TAG,"send fragment process msg in Thread " + Thread.currentThread().getId());
        Log.i(TAG,"send fragment event event type is " + event.eventType);

        switch (event.eventType){
            case XdagEvent.en_event_type_pwd:
            {
                //show dialog and ask user to type in password
                if(isVisible()){
                    Log.i(TAG,"send fragment show the auth dialog");
                    AuthDialogFragment authDialogFragment = new AuthDialogFragment();
                    authDialogFragment.setAuthHintInfo(GetAuthHintString(event.eventType));
                    authDialogFragment.show(getActivity().getFragmentManager(), "Auth Dialog");
                }
            }
            break;
            case XdagEvent.en_event_update_state:
            {
                if(event.balanceLoadState == 1){
                    mTvAvailable.setText("Available "+event.balance+" XDAG");
                }
            }
            break;
        }
    }

    private String GetAuthHintString(final int eventType){
        switch (eventType){
            case XdagEvent.en_event_set_pwd:
                return "set password";
            case XdagEvent.en_event_type_pwd:
                return "input password";
            case XdagEvent.en_event_retype_pwd:
                return "retype password";
            case XdagEvent.en_event_set_rdm:
                return "set random keys";
            default:
                return "input password";
        }
    }

    @Override
    public void onAuthInputComplete(String authInfo) {
        Log.i(TAG,"auth info is " + authInfo);
        //notify native thread
        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
        xdagWrapper.XdagNotifyMsg(authInfo);
    }
}
