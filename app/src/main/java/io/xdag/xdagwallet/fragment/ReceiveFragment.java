package io.xdag.xdagwallet.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.XdagPaymentURI;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wallet.WalletUtils;


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
        WalletUtils.loadAddress(mContext);
        String address = XdagConfig.getInstance().getAddress();
        if(address==null||address.length()==0){
            address = Config.getAddress();
        }
        String receiveUrl = new XdagPaymentURI.Builder().address(address).build().getURI();
        mImgQrAddress.setImageBitmap(ZbarUtil.createQRCode(receiveUrl));//设置二维码
        mTvAddress.setText(address);
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
