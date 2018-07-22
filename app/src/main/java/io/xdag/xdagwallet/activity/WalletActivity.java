package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;

public class WalletActivity extends ToolbarActivity {

    @BindView(R.id.wallet_tv_function_text)
    TextView mTvFunction;

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


        mTvFunction.setText(
                new TextStyleUtil()
                        .append("创建钱包：为您创建一个新的钱包来存储和交易您的 XAG。\n")
                        .append("恢复钱包：如果您创建过 XDAG 钱包，可以从这里导入钱包文件来恢复钱包。\n")
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

    public static void start(Activity context) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
        if (Config.isUserBackup() && Config.isNotShowExplain()) {
            context.finish();
        }
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.function_explain;
    }

    @Override
    protected int getToolbarMode() {
        if (Config.isUserBackup() && Config.isNotShowExplain()) {
            return ToolbarMode.MODE_NONE;
        }
        return ToolbarMode.MODE_BACK;
    }
}
