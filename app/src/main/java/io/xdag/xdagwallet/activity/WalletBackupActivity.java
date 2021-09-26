package io.xdag.xdagwallet.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;

import io.xdag.xdagwallet.view.InputPwdDialog;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */



// 进行备份提示

public class WalletBackupActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_backup)
    TextView btnBackup;
    @BindView(R.id.btn_backup_help)
    TextView btnBackupHelp;

    @BindView(R.id.iv_btn)
    TextView btnSkip;

    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private String walletMnemonic;
    private long walletId;
    private boolean firstAccount;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("备份钱包");
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        walletPwd = intent.getStringExtra("walletPwd");
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        firstAccount = getIntent().getBooleanExtra("first_account", false);
        Log.d("WalletBackupActivity", "walletMnemonic:" + walletMnemonic);
    }

    @Override
    public void configViews() {
        btnSkip.setText("跳过");
    }

    @OnClick({R.id.btn_backup, R.id.btn_backup_help, R.id.iv_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
                inputPwdDialog = new InputPwdDialog(this);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.isEmpty(pwd)) {
                            ToastUtil.show("请输入密码");
                            return;
                        }
                        if (TextUtils.equals(pwd, walletPwd)) {

                            if (firstAccount) {
                                Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            Intent intent = new Intent(WalletBackupActivity.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletMnemonic", walletMnemonic);
                            startActivity(intent);
                            inputPwdDialog.dismiss();
                            finish();
                        }
                    }
                });
                inputPwdDialog.show();
                break;
            case R.id.iv_btn:
                if (firstAccount) {
                    Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                finish();
                break;
            case R.id.btn_backup_help:
                break;
        }
    }

}
