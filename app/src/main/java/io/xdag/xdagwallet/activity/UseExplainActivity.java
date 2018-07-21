package io.xdag.xdagwallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by ssyijiu  on 2018/7/22
 */

public class UseExplainActivity extends ToolbarActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.explain_tv_explain_text)
    TextView mTvExplain;
    @BindView(R.id.explain_cb_backup)
    CheckBox mCbBackup;
    @BindView(R.id.explain_cb_no_show)
    CheckBox mCbNoShow;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_use_explain;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        mTvExplain.setText(
                new TextStyleUtil()
                        .append("XDAG Android 钱包目前还处于内测版本\n")
                        .append("使用请注意：\n")
                        .append("请务必在其他位置备份好您的钱包文件，以防止因本钱包的 bug 造成您的资产损失。\n")
                        .setForegroundColor(Common.getColor(R.color.RED))
                        .append("任何卸载、删除数据的行为，都将造成钱包文件丢失，且无法找回。\n")
                        .append("出现任何资产损失，作者概不负责。\n")
                        .setForegroundColor(Common.getColor(R.color.RED))
                        .create()
        );

        mCbBackup.setOnCheckedChangeListener(this);
        mCbNoShow.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCbBackup.setChecked(Config.isUserBackup());
        mCbNoShow.setChecked(Config.isNotShowExplain());

        if (Config.isUserBackup() && Config.isNotShowExplain()) {
            WalletActivity.start(mContext);
        }
    }

    @OnClick(R.id.explain_btn_start)
    void explain_btn_start() {
        if (!Config.isUserBackup()) {
            AlertUtil.show(mContext, getString(R.string.please_backup_your_xdag_wallet_first));
            return;
        }
        WalletActivity.start(mContext);
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.use_explain;
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
            case R.id.explain_cb_no_show:
                Config.setNotShowExplain(isChecked);
                break;
            default:

        }
    }
}
