package io.xdag.xdagwallet.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.io.File;
import java.util.Arrays;

import butterknife.OnClick;
import io.xdag.common.util.SDCardUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.activity.RestoreActivity;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

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
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
    }


    @OnClick(R.id.setting_backup)
    void setting_backup() {

        if (isSDCardWalletExists()) {
            mBuilder.create().show();
        } else {
            backupWallet();
        }
    }


    @OnClick(R.id.setting_restore)
    void setting_restore() {
        RestoreActivity.start(mContext);
    }


    @OnClick(R.id.setting_about)
    void setting_about() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://xdag.io/");
        intent.setData(content_url);
        startActivity(intent);
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
