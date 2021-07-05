package io.xdag.xdagwallet.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.wallet.CreateWalletInteract;
import io.xdag.xdagwallet.wallet.Wallet;
import io.xdag.xdagwallet.wallet.WalletUtils;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class CreateWalletActivity extends BaseActivity {

    private static final int CREATE_WALLET_RESULT = 2202;
    private static final int LOAD_WALLET_REQUEST = 1101;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;

    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.btn_create_wallet)
    TextView btnCreateWallet;

    private CreateWalletInteract createWalletInteract;

    private static final int REQUEST_WRITE_STORAGE = 112;


    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("创建钱包");
    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnCreateWallet.setEnabled(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .navigationBarColor(R.color.white)
                .init();


        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {

                    ToastUtil.show("The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission");
                }
            }
        }

    }

    @OnClick({R.id.tv_agreement, R.id.btn_create_wallet
            , R.id.lly_wallet_agreement, R.id.btn_input_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_agreement:
                break;
            case R.id.btn_create_wallet:
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(walletPwd, confirmPwd);
                if (verifyWalletInfo) {
                    showDialog("创建钱包中...");
                    createWalletInteract.create(walletPwd, confirmPwd,this).subscribe(this::jumpToWalletBackUp, this::showError);

                }
                break;
            case R.id.lly_wallet_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                } else {
                    cbAgreement.setChecked(true);
                }
                break;
            case R.id.btn_input_wallet:
//                Intent intent = new Intent(this, ImportWalletActivity.class);
//                startActivityForResult(intent, LOAD_WALLET_REQUEST);
                break;
        }
    }

    private boolean verifyInfo(String walletPwd, String confirmPwd) {

        if (TextUtils.isEmpty(walletPwd)) {
            ToastUtil.show("密码不能为空");
            // 同时判断强弱
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtil.show("密码不一致请重新输入");
            return false;
        }
        return true;
    }


    public void showError(Throwable errorInfo) {
        WalletUtils.walletDelete("/data/data/io.xdag.xdagwallet/files/xdag");
        dismissDialog();
        Log.e("CreateWalletActivity", errorInfo.getMessage());
        ToastUtil.show(errorInfo.toString());
    }

    public void jumpToWalletBackUp(Wallet wallet) {
        ToastUtil.show("创建钱包成功");
        dismissDialog();

        boolean firstAccount = getIntent().getBooleanExtra("first_account", false);

        setResult(CREATE_WALLET_RESULT, new Intent());
        Intent intent = new Intent(this, WalletBackupActivity.class);
        intent.putExtra("walletPwd", wallet.getPassword());
        intent.putExtra("walletMnemonic", wallet.getMnemonicPhrase());
        intent.putExtra("first_account", true);
        startActivity(intent);
        finish();
    }


}
