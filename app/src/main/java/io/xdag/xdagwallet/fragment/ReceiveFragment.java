package io.xdag.xdagwallet.fragment;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.dialog.LoadingBuilder;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class ReceiveFragment extends BaseMainFragment {

    @BindView(R.id.receive_tv_address)
    TextView mTvAddress;
    @BindView(R.id.receive_img_qrcode)
    ImageView mImgQrAddress;

    private AlertDialog mLoadingDialog;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mLoadingDialog = new LoadingBuilder(mContext)
                .setMessage(R.string.please_wait_read_wallet).create();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        switch (event.eventType) {
            case XdagEvent.en_event_update_state: {
                mTvAddress.setText(event.address);
                if (event.addressLoadState == XdagEvent.en_address_ready) {
                    mImgQrAddress.setImageBitmap(ZbarUtil.createQRCode(event.address));
                } else {
                    mImgQrAddress.setImageDrawable(
                            getResources().getDrawable(R.drawable.pic_loading));
                }
                if (isVisible()) {
                    // cannot connect to pool
                    if (getXdagHandler().isNotConnectedToPool(event)) {
                        mLoadingDialog.show();
                    } else {
                        mLoadingDialog.dismiss();
                    }
                }
            }
            break;
            default:
        }
    }


    @OnClick({R.id.receive_tv_copy, R.id.receive_tv_address})
    void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override
    public int getPosition() {
        return 1;
    }


    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }
}
