package io.xdag.xdagwallet.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagEventManager;

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

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        XdagEventManager.getInstance(getMainActivity()).addOnEventUpdateCallback(new XdagEventManager.OnEventUpdateCallback() {
            @Override
            public void onAddressReady(XdagEvent event) {
            }

            @Override
            public void onEventUpdate(XdagEvent event) {
                mTvAddress.setText(event.address);
                if (event.addressLoadState == XdagEvent.en_address_ready) {
                    mImgQrAddress.setImageBitmap(ZbarUtil.createQRCode(event.address));
                } else {
                    mImgQrAddress.setImageDrawable(
                            getResources().getDrawable(R.drawable.pic_loading));
                }
            }

            @Override
            public void onEventXfer(XdagEvent event) {
            }
        });
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
