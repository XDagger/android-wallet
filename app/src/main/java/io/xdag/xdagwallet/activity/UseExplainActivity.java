package io.xdag.xdagwallet.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.scottyab.rootbeer.RootBeer;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.MLog;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.model.UpdateModel;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.RxUtil;

/**
 * created by ssyijiu  on 2018/7/22
 */

public class UseExplainActivity extends ToolbarActivity
        implements CompoundButton.OnCheckedChangeListener {

    private Disposable mDisposable;

    @BindView(R.id.explain_tv_explain_text)
    TextView mTvExplain;
    @BindView(R.id.explain_cb_backup)
    CheckBox mCbBackup;
    @BindView(R.id.explain_cb_not_show)
    CheckBox mCbNoShow;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_use_explain;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mTvExplain.setText(
                new TextStyleUtil()
                        .append("XDAG Android 钱包目前还处于测试版本\n")
                        .append("使用请注意：\n")
                        .append("请务必在其他位置备份好您的钱包文件，以防止因本钱包的 bug 造成您的资产损失。\n")
                        .setForegroundColor(Common.getColor(R.color.RED))
                        .append("出现任何资产损失，作者不承担这个责任。\n")
                        .append("任何卸载、删除数据的行为，都将造成钱包文件丢失，且无法找回。\n")
                        .setForegroundColor(Common.getColor(R.color.RED))
                        .appendLine()
                        .append("为保证钱包事物的连续性，目前的版本应用内的很多弹框都不可取消，请您见谅。\n")
                        .append("我们在后续版本中将改善这个问题。")
                        .create()
        );

        mCbBackup.setOnCheckedChangeListener(this);
        mCbNoShow.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mDisposable = ApiServer.updateApi().getVersionInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UpdateModel>() {
                    @Override
                    public void accept(UpdateModel updateModel) throws Exception {

                    }
                },new ErrorConsumer(mContext));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCbBackup.setChecked(Config.isUserBackup());
        mCbNoShow.setChecked(Config.isNotShowExplain());
        RootBeer rootBeer = new RootBeer(mContext);

        // root
        if (rootBeer.isRootedWithoutBusyBoxCheck()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.check_root_explain)
                    .setPositiveButton(R.string.continue_use, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNotShow()) {
                                WalletActivity.start(mContext);
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.finish();
                        }
                    });
            builder.create().show();
        } else if (isNotShow()) {
            WalletActivity.start(mContext);
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
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


    public static boolean isNotShow() {
        return Config.isUserBackup() && Config.isNotShowExplain();
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
            case R.id.explain_cb_not_show:
                Config.setNotShowExplain(isChecked);
                break;
            default:

        }
    }
}
