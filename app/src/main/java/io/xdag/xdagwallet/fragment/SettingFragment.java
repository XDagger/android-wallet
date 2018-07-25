package io.xdag.xdagwallet.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import butterknife.OnClick;
import io.xdag.common.util.IntentUtil;
import io.xdag.common.util.SDCardUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.activity.RestoreActivity;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;
import java.io.File;
import java.util.Arrays;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SettingFragment extends BaseMainFragment {

    AlertDialog.Builder mBuilder;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }


    @Override protected boolean enableEventBus() {
        return false;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        mBuilder = new AlertDialog.Builder(mContext)
            .setTitle("警告")
            .setMessage("检测到 /sdcard/xdag 已经存在一个钱包，继续备份将覆盖这个钱包（钱包被覆盖意味着钱包的资产丢失且无法找回）。")
            .setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    backupWallet();
                }
            })
            .setNegativeButton(R.string.cancel, null);
    }


    @OnClick(R.id.setting_backup)
    void setting_backup() {

        if (isSDCardWalletExists()) {
            mBuilder.create().show();
        } else {
            backupWallet();
        }
    }


    @OnClick(R.id.setting_switch)
    void setting_switch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
            .setMessage("这个功能我们正在开发中，请稍后。")
            .setPositiveButton(R.string.ensure, null);
        builder.create().show();
    }


    @OnClick(R.id.setting_suggest)
    void setting_suggest() {
        IntentUtil.openBrowser(mContext, "http://cn.mikecrm.com/im90MMt");
    }


    @OnClick(R.id.setting_open_source)
    void setting_open_source() {
        IntentUtil.openBrowser(mContext, "https://github.com/ssyijiu/xdagwallet");
    }


    @OnClick(R.id.setting_about)
    void setting_about() {
        IntentUtil.openBrowser(mContext, "https://xdag.io/");
    }


    private void backupWallet() {
        if (getXdagHandler().backupWallet()) {
            AlertUtil.show(mContext, R.string.success_backup_xdag_wallet);
        } else {
            AlertUtil.show(mContext, R.string.error_backup_xdag_wallet);
        }
    }


    private boolean isSDCardWalletExists() {
        if (SDCardUtil.isAvailable()) {
            File file = new File(SDCardUtil.getSDCardPath(), XdagHandlerWrapper.XDAG_FILE);
            return file.exists() &&
                Arrays.asList(file.list()).containsAll(XdagHandlerWrapper.WALLET_LIST);
        }
        return false;
    }


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    public int getPosition() {
        return 3;
    }
}
