package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.util.DensityUtil;
import io.xdag.common.util.FileUtil;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;

import static io.xdag.xdagwallet.wrapper.XdagHandlerWrapper.XDAG_FILE;
import static io.xdag.xdagwallet.wrapper.XdagHandlerWrapper.createSDCardFile;

/**
 * created by ssyijiu  on 2018/7/22
 */

public class RestoreActivity extends ToolbarActivity {

    @BindView(R.id.restore_tv_explain)
    TextView mTvExplain;
    @BindView(R.id.restore_cb_read)
    CheckBox mCbRead;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_restore;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        int color = Common.getColor(R.color.RED);
        int gapWidth = DensityUtil.dp2px(4);

        mTvExplain.setText(
                new TextStyleUtil()
                        .append(getString(R.string.restore_explain_1))
                        .append(getString(R.string.restore_explain_2))
                        .setForegroundColor(color)
                        .appendLine()
                        .append(getString(R.string.restore_explain_3))
//                        .append("storage\n").setBullet(gapWidth)
//                        .setForegroundColor(color)
//                        .append("dnet_key.dat\n").setBullet(gapWidth)
                        .append("address.dat\n").setBullet(gapWidth)
                        .setForegroundColor(color)
                        .append("newwallet.dat\n").setBullet(gapWidth)
                        .setForegroundColor(color)
                        .appendLine()
                        .append(getString(R.string.restore_explain_4))
                        .append(getString(R.string.restore_explain_5))
                        .setForegroundColor(color)
                        .appendLine()
                        .append(getString(R.string.restore_explain_6))
                        .appendLine()
                        .append(getString(R.string.restore_explain_7))
                        .append(getString(R.string.restore_explain_8))
                        .create()
        );
    }


    @OnClick(R.id.restore_btn_restore)
    void restore_btn_restore() {
        if(!mCbRead.isChecked()) {
            AlertUtil.show(mContext,R.string.please_restore_read);
            return;
        }
//        MainActivity.start(mContext, true);
        if(restoreWallet()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //this.finish();
        }
        else {
            AlertUtil.show(this, R.string.error_file_make_fail);
        }

    }

    @Override
    protected int getToolbarTitle() {
        return R.string.restore_wallet;
    }

    public static void start(Activity context) {
        Intent intent = new Intent(context, RestoreActivity.class);
        context.startActivity(intent);
    }

    public boolean restoreWallet() {

        File tempFile = createSDCardFile(this);
        File xdagFile = createXdagFile();

        return tempFile != null && xdagFile != null && FileUtil.moveDir(tempFile, xdagFile);
    }

    private File createXdagFile() {

        File file = new File(this.getFilesDir(), XDAG_FILE);
        if (!file.exists() && !file.mkdirs()) {
            AlertUtil.show(this, R.string.error_file_make_fail);
        } else {
            return file;
        }
        return null;
    }
}
