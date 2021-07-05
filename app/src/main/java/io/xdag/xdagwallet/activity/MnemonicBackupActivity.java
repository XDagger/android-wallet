package io.xdag.xdagwallet.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.xdagwallet.R;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */



// 提示 抄下钱包助记词 界面

public class MnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_MNEMONIC_BACKUP_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_mnemonic)
    TextView tvMnemonic;
    private String walletMnemonic;

    private long walletId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("备份助记词");
    }

    @Override
    public void initDatas() {
//        MnemonicBackupAlertDialog mnemonicBackupAlertDialog = new MnemonicBackupAlertDialog(this);
//        mnemonicBackupAlertDialog.show();
        Intent intent = getIntent();
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        tvMnemonic.setText(walletMnemonic);

    }

    @Override
    public void configViews() {

    }

    @OnClick(R.id.btn_next)
    public void onClick(View view) {
        Intent intent = new Intent(this, VerifyMnemonicBackupActivity.class);
        intent.putExtra("walletMnemonic", walletMnemonic);
        startActivityForResult(intent, VERIFY_MNEMONIC_BACKUP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_MNEMONIC_BACKUP_REQUEST) {
            if (data != null) {
                finish();
            }
        }
    }
}

