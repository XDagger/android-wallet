package io.xdag.xdagwallet.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import io.xdag.common.base.BaseActivity;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;

public class WalletActivity extends BaseActivity {

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
