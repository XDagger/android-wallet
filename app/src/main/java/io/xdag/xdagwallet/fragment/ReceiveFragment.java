package io.xdag.xdagwallet.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.QRUtils;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.wrapper.XdagEvent;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class ReceiveFragment extends BaseFragment {

    private static final String TAG = "XdagWallet";

    @BindView(R.id.receive_tv_address) TextView mTvAddress;
    @BindView(R.id.receive_img_qrcode) ImageView mImgQrAddress;
    private Handler mXdagMessageHandler;

    public void setMessagehandler(Handler xdagMessageHandler){
        this.mXdagMessageHandler = xdagMessageHandler;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    @OnClick({ R.id.receive_tv_copy, R.id.receive_tv_address }) void copyAddress() {
        ((MainActivity) mContext).copyText(mTvAddress.getText().toString());
    }

    public static ReceiveFragment newInstance() {
        ReceiveFragment fragment = new ReceiveFragment();
        EventBus.getDefault().register(fragment);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        Log.i(TAG,"process msg in Thread " + Thread.currentThread().getId());
        Log.i(TAG,"event event type is " + event.eventType);
        Log.i(TAG,"event account is " + event.address);
        Log.i(TAG,"event balace is " + event.balance);
        Log.i(TAG,"event state is " + event.state);

        switch (event.eventType){
            case XdagEvent.en_event_update_state:
            {
                Log.i(TAG,"update xdag  ui ");
                mTvAddress.setText(event.address);
                if(event.addressLoadState == 1){
                    Bitmap qrCode = QRUtils.getInstance().createQRCode(event.address);
                    mImgQrAddress.setImageBitmap(qrCode);
                }else{
                    mImgQrAddress.setImageDrawable(getResources().getDrawable(R.drawable.qrcode_loading));
                }
            }
            break;
        }
    }
}
