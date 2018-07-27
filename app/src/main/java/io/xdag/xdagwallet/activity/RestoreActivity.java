package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.util.DensityUtil;
import io.xdag.common.util.TextStyleUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;

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
                        .append("XDAG 因为其独特的设计，\n")
                        .append("目前不支持私钥、助记词等方式备份和恢复钱包。\n")
                        .setForegroundColor(color)
                        .appendLine()
                        .append("您只能通过以下钱包文件来备份和恢复您的钱包：\n")
                        .append("storage\n").setBullet(gapWidth)
                        .setForegroundColor(color)
                        .append("dnet_key.dat\n").setBullet(gapWidth)
                        .setForegroundColor(color)
                        .append("wallet.dat\n").setBullet(gapWidth)
                        .setForegroundColor(color)
                        .appendLine()
                        .append("恢复钱包需要将您的三个钱包文件拷贝到存储卡根目录下的 xdag 文件夹。\n")
                        .append("存储卡并不是一个安全的位置，任何恶意程序都可以轻而易举的从存储卡将您的钱包拷贝走。\n")
                        .setForegroundColor(color)
                        .appendLine()
                        .append("因此，在您点击恢复钱包的按钮后，我们会将您的钱包文件移动到 Android 系统中最为安全的私有目录。\n")
                        .appendLine()
                        .append("您可以随时在设置页面将您的钱包备份回存储卡。\n")
                        .append("但是请切记，存储卡不是一个安全的位置，备份后请您快速转移钱包文件，以免造成资产损失。")
                        .create()
        );
    }


    @OnClick(R.id.restore_btn_restore)
    void restore_btn_restore() {
        if(!mCbRead.isChecked()) {
            AlertUtil.show(mContext,R.string.please_restore_read);
            return;
        }
        MainActivity.start(mContext, true);
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.restore_wallet;
    }

    public static void start(Activity context) {
        Intent intent = new Intent(context, RestoreActivity.class);
        context.startActivity(intent);
    }
}
