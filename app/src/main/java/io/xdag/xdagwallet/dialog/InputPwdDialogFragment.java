package io.xdag.xdagwallet.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.xdag.xdagwallet.R;


public class InputPwdDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String PASSWORD = "Password";
    private EditText mPwdEditText;
    private TextView mTitileTextView;
    private Button mOkButton;
    private Button mCancelButton;
    public InputPwdDialogFragment(){
        setCancelable(false);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input_pwd, null);
        mTitileTextView = view.findViewById(R.id.title);
        mOkButton= view.findViewById(R.id.ok_action);
        mCancelButton= view.findViewById(R.id.cancel_action);
        mPwdEditText= view.findViewById(R.id.input_password);

        Bundle bundle = getArguments();
        String title = bundle.getString("title","");
        mTitileTextView.setText(title);

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_action:
                if (getTargetFragment()== null){
                    Log.e("Input","获取失败");
                    return;
                }Log.e("Input","获取成功");
                Intent intent= new Intent();
                intent.putExtra(PASSWORD, mPwdEditText.getText().toString());
                getTargetFragment().onActivityResult(this.getTargetRequestCode(), Activity.RESULT_OK, intent);
                break;
            case R.id.cancel_action:
                if (getTargetFragment()== null){
                    return;
                }
                getTargetFragment().onActivityResult(this.getTargetRequestCode(), Activity.RESULT_CANCELED,null);
                break;
        }
    }
}