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
import io.reactivex.disposables.Disposable;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ActivityStack;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.net.HttpRequest;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.RxUtil;

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
                .setForegroundColor(Common.getColor(R.color.RED))
                .append(getString(R.string.use_explain_4))
                .append(getString(R.string.use_explain_5))
                .setForegroundColor(Common.getColor(R.color.RED))
                .appendLine()
                .append(getString(R.string.use_explain_6))
                .append(getString(R.string.use_explain_7))
                .create()
        );

        mCbBackup.setOnCheckedChangeListener(this);
        mCbNoShow.setOnCheckedChangeListener(this);
        mRootRemindLayout = View.inflate(mContext, R.layout.dialog_item_checkbox, null);
        mCbRootRemind = mRootRemindLayout.findViewById(R.id.dialog_cb);
    }


    @Override protected void initData() {
        super.initData();
        mCbBackup.setChecked(Config.isUserBackup());
        mCbNoShow.setChecked(Config.isNotDisplayUsage());
        RootBeer rootBeer = new RootBeer(mContext);
        // check root
        if (Config.isRemindRoot() && !rootBeer.isRootedWithoutBusyBoxCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(R.string.warning)
                .setView(mRootRemindLayout)
                .setMessage(R.string.check_root_explain)
                .setPositiveButton(R.string.continue_use, (dialog, which) -> {
                    if (mCbRootRemind.isChecked()) {
                        Config.setNotRemindRoot(true);
                    }
                    if (isNotShow()) {
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
        } else if (isNotShow()) {
            WalletActivity.start(mContext);
            finish();
        } else {
            mDisposable = HttpRequest.get().getConfigInfo(configModel
                -> Config.setTransactionHost(configModel.transactionHost));
        }
    }


    @OnClick(R.id.explain_btn_start)
    void explain_btn_start() {
        if (!Config.isUserBackup()) {
            AlertUtil.show(mContext, getString(R.string.please_backup_your_xdag_wallet_first));
            return;
        }
        WalletActivity.start(mContext);
        if (isNotShow()) {
            finish();
        }
    }


    @OnClick(R.id.explain_btn_pool)
    void explain_btn_pool() {
        PoolListActivity.start(mContext, true);
    }


    public static boolean isNotShow() {
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
        if (!MainActivity.isStart) {
            ActivityStack.getInstance().exit();
        }
    }
}
