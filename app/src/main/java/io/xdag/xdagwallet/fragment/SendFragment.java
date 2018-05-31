package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import cn.bertsir.zbar.QRManager;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ZbarUtil;
import java.util.List;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.send_et_address) EditText mEtAddress;


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


    public static SendFragment newInstance() {
        return new SendFragment();
    }
}
