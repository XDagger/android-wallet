package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

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

        if (isWalletExists()) {
            MainActivity.start(mContext, false);
            return;
        }

        mTvFunction.setText(
                new TextStyleUtil()
                        .append("未检测到您的 XDAG 钱包文件，\n")
                        .append("现在您可以使用以下两种方式获取 XDAG 钱包。\n")
                        .appendLine()
                        .append("创建钱包：为您创建一个新的钱包来存储和交易您的 XAG。\n")
                        .append("恢复钱包：如果您创建过 XDAG 钱包，可以从这里导入钱包文件来恢复钱包。\n")
                        .appendLine()
                        .append("在点击恢复钱包后，我们会帮您在存储卡根目录创建一个 xdag 文件夹，这个时候我们会请求您的存储卡权限，请您不要拒绝。\n")
                        .create()
        );
    }

    /**
     * check xdag wallet is or not exists
     */
    private boolean isWalletExists() {
        File file = new File(mContext.getFilesDir(), XdagHandlerWrapper.XDAG_FILE);
        return file.exists() && Arrays.asList(file.list()).containsAll(XdagHandlerWrapper.WALLET_LIST);
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
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (XdagHandlerWrapper.createSDCardFile(mContext) != null) {
                            RestoreActivity.start(mContext);
                        }
                    }
                })
                .start();
    }


    public static void start(Activity context, boolean finish) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
        if (finish) {
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
