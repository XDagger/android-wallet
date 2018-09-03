package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import io.xdag.xdagwallet.util.XdagPaymentURI;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.QRManager;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagEventManager;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.send_et_amount)
    EditText mEtAmount;
    @BindView(R.id.send_et_address)
    EditText mEtAddress;
    @BindView(R.id.send_btn_xdag)
    Button mBtnSendXdag;
    @BindView(R.id.send_tv_available)
    TextView mTvAvailable;

    private String mBalance;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        getToolbar().inflateMenu(R.menu.toolbar_scan);
        getToolbar().setOnMenuItemClickListener(this);

        XdagEventManager.getInstance(getMainActivity()).addOnEventUpdateCallback(new XdagEventManager.OnEventUpdateCallback() {
            @Override
            public void onAddressReady(XdagEvent event) {

            }

            @Override
            public void onEventUpdate(XdagEvent event) {
                if (event.balanceLoadState == XdagEvent.en_balance_ready) {
                    mBalance = event.balance;
                    mTvAvailable.setText(getString(R.string.available_xdag, mBalance));
                }
            }

            @Override
            public void onEventXfer(XdagEvent event) {

            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
                    .onGranted(data -> ZbarUtil.startScan(mContext, new QRManager.OnScanResultCallback() {
                        @Override
                        public void onScanSuccess(String result) {
                            XdagPaymentURI xdagPaymentURI = XdagPaymentURI.parse(result);
                            if(xdagPaymentURI != null) {
                                String address = xdagPaymentURI.getAddress();
                                if(!TextUtils.isEmpty(address)) {
                                    mEtAddress.setText(address);
                                }
                                Double amount = xdagPaymentURI.getAmount();
                                if(amount != null) {
                                    mEtAmount.setText(String.valueOf(amount));
                                }
                            }
                        }


                        @Override
                        public void onScanFailed() {
                            AlertUtil.show(mContext, R.string.error_cannot_identify_qr_code);
                        }
                    }))
                    .start();
        }
        return false;
    }


    @OnClick({R.id.send_btn_xdag})
    void send_btn_xdag() {
        String address = mEtAddress.getText().toString();
        String amount = mEtAmount.getText().toString();
        getXdagHandler().xferXdagCoin(address, amount);
    }

    @OnClick({R.id.send_tv_available})
    void send_tv_available() {
        mEtAmount.setText(mBalance);
    }

    public static SendFragment newInstance() {
        return new SendFragment();
    }


    @Override
    public int getPosition() {
        return 2;
    }
}
