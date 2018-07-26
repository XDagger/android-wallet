package io.xdag.xdagwallet.fragment;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.dialog.InputBuilder;
import io.xdag.xdagwallet.dialog.LoadingBuilder;
import io.xdag.xdagwallet.dialog.TipBuilder;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagWrapper;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.send_et_amount) EditText mEtAmount;
    @BindView(R.id.send_et_address) EditText mEtAddress;
    @BindView(R.id.send_btn_xdag) Button mBtnSendXdag;
    @BindView(R.id.send_tv_available) TextView mTvAvailable;

    private AlertDialog mLoadingDialog;
    private AlertDialog mInputDialog;
    private AlertDialog mTipDialog;
    private LoadingBuilder mLoadingBuilder;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        getToolbar().inflateMenu(R.menu.toolbar_scan);
        getToolbar().setOnMenuItemClickListener(this);
        mLoadingBuilder = new LoadingBuilder(mContext);
        mLoadingDialog = mLoadingBuilder.create();

        mLoadingDialog = new LoadingBuilder(mContext)
            .setMessage(R.string.please_wait_connecting_pool).create();

        mInputDialog = new InputBuilder(mContext)
            .setPositiveListener(new InputBuilder.OnPositiveClickListener() {
                @Override
                public void onClick(DialogInterface dialog, String input) {
                    if (input.length() < 6) {
                        AlertUtil.show(mContext, R.string.error_password_format);
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                mInputDialog.show();
                            }
                        }, 500);
                    } else {
                        XdagWrapper.getInstance().XdagNotifyMsg(input);
                        dialog.dismiss();
                        mLoadingDialog.show();
                    }
                }

            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    XdagWrapper.getInstance().XdagNotifyMsg("");
                }
            })
            .setMessage(R.string.please_input_password)
            .create();

        mTipDialog = new TipBuilder(mContext)
            .setPositiveListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    XdagWrapper.getInstance().XdagNotifyMsg("");
                    dialog.dismiss();
                    mLoadingBuilder.setMessage(R.string.please_wait_transfer);
                    mLoadingDialog.show();
                }
            }).create();
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
                                AlertUtil.show(mContext, R.string.error_cannot_identify_qr_code);
                            }
                        });
                    }
                })
                .start();
        }
        return false;
    }


    @OnClick({ R.id.send_btn_xdag }) void sendXdag() {
        String address = mEtAddress.getText().toString();
        String amount = mEtAmount.getText().toString();
        getXdagHandler().xferXdagCoin(address, amount);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        switch (event.eventType) {
            case XdagEvent.en_event_type_pwd: {
                if (isVisible()) {
                    mLoadingDialog.dismiss();
                    mInputDialog.show();
                }
            }
            break;
            case XdagEvent.en_event_update_state: {

                if (event.balanceLoadState == XdagEvent.en_balance_ready) {
                    mTvAvailable.setText(getString(R.string.available_xdag, event.balance));
                }
                if (isVisible()) {
                    if (getXdagHandler().isNotConnectedToPool(event)) {
                        mLoadingDialog.show();
                    } else {
                        mLoadingDialog.dismiss();
                    }
                }
            }
            break;
            case XdagEvent.en_event_pwd_error:
            case XdagEvent.en_event_nothing_transfer:
            case XdagEvent.en_event_balance_too_small:
            case XdagEvent.en_event_invalid_recv_address: {
                MLog.i("Event: password error");
                if (isVisible()) {
                    mLoadingDialog.dismiss();
                    String message = "";
                    if (event.eventType == XdagEvent.en_event_pwd_error) {
                        message = getString(R.string.error_password);
                    } else if (event.eventType == XdagEvent.en_event_nothing_transfer) {
                        message = getString(R.string.error_noting_to_transfer);
                    } else if (event.eventType == XdagEvent.en_event_balance_too_small) {
                        message = getString(R.string.error_balance_too_small);
                    } else if (event.eventType == XdagEvent.en_event_invalid_recv_address) {
                        message = getString(R.string.error_invalid_recv_address);
                    }
                    mTipDialog.setMessage(message);
                    mTipDialog.show();
                }
            }
            break;
        }
    }


    public static SendFragment newInstance() {
        return new SendFragment();
    }


    @Override
    public int getPosition() {
        return 2;
    }
}
