package io.xdag.xdagwallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.Common;
import io.xdag.common.base.BaseActivity;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;

public class WalletActivity extends BaseActivity {

    @BindView(R.id.wallet_tv_explain_text)
    TextView mTvExplain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wallet;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mTvExplain.setText(
                new TextStyleUtil()
                        .append("XDAG Android 钱包目前还处于内测版本\n")
                        .append("使用请注意：\n")
                        .append("请务必在其他位置备份好您的钱包文件，\n")
                        .setForegroundColor(Common.getColor(R.color.RED))
                        .setFontSize(18, true)
                        .append("以防止因本钱包的 bug 造成您的财产损失。\n")
                        .append("任何卸载、删除数据的行为，都将造成钱包文件丢失，且无法找回。\n")
                        .create()
        );
    }


    @OnClick(R.id.wallet_btn_create)
    void wallet_btn_create() {
        toMainActivity(false);
    }


    @OnClick(R.id.wallet_btn_restore)
    void wallet_btn_restore() {
        toMainActivity(true);
    }


    private void toMainActivity(boolean restore) {
        Config.setRestore(restore);
        MainActivity.start(mContext);
        finish();
    }
}
