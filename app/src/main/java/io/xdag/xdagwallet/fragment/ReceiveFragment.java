package io.xdag.xdagwallet.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.QRUtils;
import io.xdag.common.util.DialogUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class ReceiveFragment extends BaseMainFragment {

    private static final String TAG = "XdagWallet";

    @BindView(R.id.receive_tv_address) TextView mTvAddress;
    @BindView(R.id.receive_img_qrcode) ImageView mImgQrAddress;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    @OnClick({ R.id.receive_tv_copy, R.id.receive_tv_address }) void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    public static ReceiveFragment newInstance() {
        ReceiveFragment fragment = new ReceiveFragment();
        EventBus.getDefault().register(fragment);
        return fragment;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        Log.i(TAG, "receive fragment process msg in Thread " + Thread.currentThread().getId());

        switch (event.eventType) {

            case XdagEvent.en_event_update_state: {
                mTvAddress.setText(event.address);
                if (event.addressLoadState == 1) {
                    Bitmap qrCode = QRUtils.getInstance().createQRCode(event.address);
                    mImgQrAddress.setImageBitmap(qrCode);
                } else {
                    mImgQrAddress.setImageDrawable(
                        getResources().getDrawable(R.drawable.qrcode_loading));
                }
                if (isVisible() && !DialogUtil.isShow()) {
                    if (event.programState < XdagEvent.CONN) {
                        DialogUtil.showLoadingDialog(getMainActivity(), "Loading......", false);
                    } else {
                        DialogUtil.dismissLoadingDialog();
                    }
                }
            }
            break;
        }
    }

}
