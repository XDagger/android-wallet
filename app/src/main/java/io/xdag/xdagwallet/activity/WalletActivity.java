package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

public class WalletActivity extends ToolbarActivity {

    @BindView(R.id.wallet_tv_function_text)
    TextView mTvFunction;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wallet;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        if (isWalletExists()) {
            MainActivity.start(mContext, false);
            return;
        }

        mTvFunction.setText(
            new TextStyleUtil()
                .append(getString(R.string.wallet_explain_1))
                .append(getString(R.string.wallet_explain_2))
                .appendLine()
                .append(getString(R.string.wallet_explain_3))
                .append(getString(R.string.wallet_explain_4))
                .appendLine()
                .append(getString(R.string.wallet_explain_5))
                .create()
        );
    }


    /**
     * check xdag wallet is or not exists
     */
    private boolean isWalletExists() {
        File file = new File(mContext.getFilesDir(), XdagHandlerWrapper.XDAG_FILE);
        return file.exists() &&
            Arrays.asList(file.list()).containsAll(XdagHandlerWrapper.WALLET_LIST);
    }


    @OnClick(R.id.wallet_btn_create)
    void wallet_btn_create() {
        MainActivity.start(mContext, false);
    }


    @OnClick(R.id.wallet_btn_restore)
    void wallet_btn_restore() {
        AndPermission.with(mContext)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted(data -> {
                if (XdagHandlerWrapper.createSDCardFile(mContext) != null) {
                    RestoreActivity.start(mContext);
                }
            })
            .start();
    }


    public static void start(Activity context) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.function_explain;
    }


    @Override
    protected int getToolbarMode() {
        if (UsageActivity.isNotDisplay()) {
            return ToolbarMode.MODE_NONE;
        }
        return ToolbarMode.MODE_BACK;
    }
}
