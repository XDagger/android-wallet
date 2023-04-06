package io.xdag.xdagwallet.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.scottyab.rootbeer.RootBeer;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import io.reactivex.disposables.Disposable;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ActivityStack;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.DeviceUtils;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.net.HttpRequest;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

/**
 * created by ssyijiu  on 2018/7/22
 */

public class UsageActivity extends ToolbarActivity
    implements CompoundButton.OnCheckedChangeListener {

    private Disposable mDisposable;

    @BindView(R.id.explain_tv_explain_text)
    TextView mTvExplain;
    @BindView(R.id.explain_cb_backup)
    CheckBox mCbBackup;
    @BindView(R.id.explain_cb_not_show)
    CheckBox mCbNoShow;
    View mRootRemindLayout;
    CheckBox mCbRootRemind;

    AlertDialog.Builder mBuilder;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_use_explain;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mTvExplain.setText(
            new TextStyleUtil()
                .append(getString(R.string.use_explain_1))
                .append(getString(R.string.use_explain_2))
                .append(getString(R.string.use_explain_3))
                .append(getString(R.string.use_explain_4))
                .append(getString(R.string.use_explain_5))
                .appendLine()
                .append(getString(R.string.use_explain_6))
                .append(getString(R.string.network_upgrade_0_6_1))
                .setForegroundColor(Common.getColor(R.color.RED))
                .create()
        );

        mCbBackup.setOnCheckedChangeListener(this);
        mCbNoShow.setOnCheckedChangeListener(this);
        mRootRemindLayout = View.inflate(mContext, R.layout.dialog_item_checkbox, null);
        mCbRootRemind = mRootRemindLayout.findViewById(R.id.dialog_cb);

        mBuilder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.warning)
                .setMessage(R.string.cover_explain)
                .setPositiveButton(R.string.cover, (dialog, which) -> backupWallet())
                .setNegativeButton(R.string.cancel, null);
    }


    @Override protected void initData() {
        super.initData();
        mCbBackup.setChecked(Config.isUserBackup());
        mCbNoShow.setChecked(Config.isNotDisplayUsage());
        RootBeer rootBeer = new RootBeer(mContext);
        // check root
        if (Config.isRemindRoot() && rootBeer.isRootedWithoutBusyBoxCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(R.string.warning)
                .setView(mRootRemindLayout)
                .setMessage(R.string.check_root_explain)
                .setPositiveButton(R.string.continue_use, (dialog, which) -> {
                    if (mCbRootRemind.isChecked()) {
                        Config.setNotRemindRoot(true);
                    }
                    if (isNotDisplay()) {
                        WalletActivity.start(mContext);
                        finish();
                    }
                })
                .setNegativeButton(R.string.exit, (dialog, which) -> {
                    if (mCbRootRemind.isChecked()) {
                        Config.setNotRemindRoot(true);
                    }
                    mContext.finish();
                });
            builder.show();
        } else if (isNotDisplay()) {
            WalletActivity.start(mContext);
            finish();
        } else {
            mDisposable = HttpRequest.get().getConfigInfo(configModel
                -> Config.setTransactionHost(configModel.transactionHost));
        }
    }


    @OnClick(R.id.explain_btn_backup)
    void explain_btn_backup() {
        if (DeviceUtils.afterQ()) {
            checkBackup();
        } else {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted(data -> checkBackup())
                    .onDenied(strings -> AlertUtil.show(mContext, getString(R.string.no_file_access_permission)))
                    .start();
        }
    }

    @OnClick(R.id.explain_btn_start)
    void explain_btn_start() {
        if (!Config.isUserBackup()) {
            AlertUtil.show(mContext, getString(R.string.please_backup_your_xdag_wallet_first));
            return;
        }
        WalletActivity.start(mContext);
        if (isNotDisplay()) {
            finish();
        }
    }


    @OnClick(R.id.explain_btn_pool)
    void explain_btn_pool() {
        PoolListActivity.start(mContext, true);
    }


    private void checkBackup() {
        if (XdagHandlerWrapper.hasBackup()) {
            mBuilder.create().show();
        } else {
            backupWallet();
        }
    }

    private void backupWallet() {
        if (XdagHandlerWrapper.getInstance(this).backupWallet()) {
            AlertUtil.show(mContext, R.string.success_backup_xdag_wallet);
        } else {
            AlertUtil.show(mContext, R.string.error_backup_xdag_wallet);
        }
    }

    public static boolean isNotDisplay() {
        return Config.isUserBackup() && Config.isNotDisplayUsage();
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.usage;
    }


    @Override
    protected int getToolbarMode() {
        return ToolbarMode.MODE_NONE;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.explain_cb_backup:
                Config.setUserBackup(isChecked);
                break;
            case R.id.explain_cb_not_show:
                Config.setNotDisplayUsage(isChecked);
                break;
            default:

        }
    }


    @Override protected void onDestroy() {
        RxUtil.dispose(mDisposable);
        super.onDestroy();
    }
}
