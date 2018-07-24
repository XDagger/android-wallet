package io.xdag.xdagwallet.fragment;

import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.tool.MLog;
import io.xdag.common.util.DialogUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class ReceiveFragment extends BaseMainFragment {

    @BindView(R.id.receive_tv_address) TextView mTvAddress;
    @BindView(R.id.receive_img_qrcode) ImageView mImgQrAddress;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        MLog.i("receive fragment process msg in Thread " + Thread.currentThread().getId());
        switch (event.eventType) {
            case XdagEvent.en_event_update_state: {

                mTvAddress.setText(event.address);
                if (event.addressLoadState == XdagEvent.en_address_ready) {
                    mImgQrAddress.setImageBitmap(ZbarUtil.createQRCode(event.address));
                } else {
                    mImgQrAddress.setImageDrawable(
                        getResources().getDrawable(R.drawable.pic_loading));
                }
                if (isVisible() && !DialogUtil.isShow()) {

                    // cannot connect to pool
                    if (event.programState < XdagEvent.CONN) {
                        DialogUtil.showLoadingDialog(getMainActivity(), "Loading...", false);
                    } else {
                        DialogUtil.dismissLoadingDialog();
                    }
                }
            }
            break;
            default:
        }
    }


    @OnClick({ R.id.receive_tv_copy, R.id.receive_tv_address }) void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override
    public int getPosition() {
        return 1;
    }
}
