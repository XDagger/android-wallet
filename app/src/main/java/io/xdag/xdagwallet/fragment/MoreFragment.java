package io.xdag.xdagwallet.fragment;

import android.support.v7.app.AlertDialog;
import android.view.View;

import butterknife.OnClick;
import io.xdag.common.util.IntentUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.activity.AboutActivity;
import io.xdag.xdagwallet.activity.PoolListActivity;
import io.xdag.xdagwallet.activity.SettingActivity;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class MoreFragment extends BaseMainFragment {

    AlertDialog.Builder mBuilder;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_more;
    }


    @Override
    protected boolean enableEventBus() {
        return false;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        mBuilder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.warning)
                .setMessage(R.string.cover_explain)
                .setPositiveButton(R.string.cover, (dialog, which) -> backupWallet())
                .setNegativeButton(R.string.cancel, null);
    }


    @OnClick(R.id.more_backup)
    void setting_backup() {
//        AndPermission.with(mContext)
//                .runtime()
//                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
//                .onGranted(data -> checkBackup())
//                .onDenied(strings -> AlertUtil.show(mContext, getString(R.string.no_file_access_permission)))
//                .start();
        checkBackup();
    }

    @OnClick(R.id.more_switch_wallet)
    void setting_switch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(R.string.the_function_developing_please_wait)
                .setPositiveButton(R.string.ensure, null);
        builder.create().show();
    }


    @OnClick(R.id.more_switch_pool)
    void setting_switch_pool() {
        PoolListActivity.start(mContext, false);
    }

    @OnClick(R.id.more_setting)
    void more_setting() {
        SettingActivity.start(mContext);
    }


    @OnClick(R.id.more_suggest)
    void setting_suggest() {
        IntentUtil.openBrowser(mContext, "http://cn.mikecrm.com/im90MMt");
    }


    @OnClick(R.id.more_about)
    void setting_about() {
        AboutActivity.start(mContext);
    }


    @OnClick(R.id.more_github)
    void setting_github() {
        IntentUtil.openBrowser(mContext, "https://github.com/XDagger");
    }


    @OnClick(R.id.more_xdagio)
    void setting_xdagio() {
        IntentUtil.openBrowser(mContext, "https://xdag.io");
    }

    private void checkBackup() {
        if (XdagHandlerWrapper.hasBackup()) {
            mBuilder.create().show();
        } else {
            backupWallet();
        }
    }

    private void backupWallet() {
        if (getXdagHandler().backupWallet()) {
            AlertUtil.show(mContext, R.string.success_backup_xdag_wallet);
        } else {
            AlertUtil.show(mContext, R.string.error_backup_xdag_wallet);
        }
    }


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }


    @Override
    public int getPosition() {
        return 3;
    }
}
