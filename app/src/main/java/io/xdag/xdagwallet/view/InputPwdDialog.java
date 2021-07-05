package io.xdag.xdagwallet.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;



public class InputPwdDialog extends Dialog implements View.OnClickListener {
    EditText etPwd;
    TextView btnCancel;
    TextView btnConfirm;
    private TextView tvDeleteAlert;

    private OnInputDialogButtonClickListener onInputDialogButtonClickListener;

    public void setOnInputDialogButtonClickListener(OnInputDialogButtonClickListener onInputDialogButtonClickListener) {
        this.onInputDialogButtonClickListener = onInputDialogButtonClickListener;
    }


    public InputPwdDialog(@NonNull Context context) {
        super(context);
    }

    public InputPwdDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_pwd2);
        setCanceledOnTouchOutside(false);
        initView();
        //初始化界面控件的事件
        initEvent();
    }

    private void initView() {
        etPwd = (EditText)findViewById(R.id.et_pwd);
        btnCancel = (TextView)findViewById(R.id.btn_cancel);
        btnConfirm = (TextView)findViewById(R.id.btn_confirm);
        tvDeleteAlert = (TextView)findViewById(R.id.tv_delete_alert);
    }


    private void initEvent() {
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    public void setDeleteAlertVisibility(boolean visibility) {
        if (tvDeleteAlert != null)
            if (visibility) {
                tvDeleteAlert.setVisibility(View.VISIBLE);
            } else {
                tvDeleteAlert.setVisibility(View.GONE);
            }
    }


    @Override
    public void onClick(View v) {
        if (onInputDialogButtonClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_cancel:// 取消
                    onInputDialogButtonClickListener.onCancel();
                    break;
                case R.id.btn_confirm:// 确定
                    String pwd = etPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        ToastUtil.show("请输入密码");
                        return;
                    }
                    onInputDialogButtonClickListener.onConfirm(pwd);
                    break;
            }
        }
    }

    public interface OnInputDialogButtonClickListener {
        void onCancel();

        void onConfirm(String pwd);
    }

    @Override
    protected void onStop() {
        super.onStop();
        etPwd.setText("");
    }
}
