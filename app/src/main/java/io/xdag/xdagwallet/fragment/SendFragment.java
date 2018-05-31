package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import cn.bertsir.zbar.QRManager;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.ZbarUtil;
import java.util.List;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener {

    public static SendFragment newInstance() {
        return new SendFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override protected void initView(View rootView) {
        super.initView(rootView);
        getToolbar().inflateMenu(R.menu.toolbar_scan);
        getToolbar().setOnMenuItemClickListener(this);
    }


    @Override public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {

            AndPermission.with(mContext)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
                .onDenied(new Action<List<String>>() {
                    @Override public void onAction(List<String> data) {
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override public void onAction(List<String> data) {
                        ZbarUtil.startScan(mContext, new QRManager.OnScanResultCallback() {
                            @Override public void onScanSuccess(String result) {

                            }

                            @Override public void onScanFailed() {

                            }
                        });
                    }
                })
                .start();

        }
        return false;
    }

}
