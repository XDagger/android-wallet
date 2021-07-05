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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.xdag.xdagwallet.R;


public class SetPwdDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String PASSWORD1 = "Password1";
    public static final String PASSWORD2 = "Password2";
    private TextView mTitileTextView;
    private EditText mTypePwdEditText;
    private EditText mReTypePwdEditText;
    private Button mOkButton;

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
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_pwd, null);
        mOkButton= view.findViewById(R.id.ok_action);
        mTitileTextView=view.findViewById(R.id.title);
        mTypePwdEditText= view.findViewById(R.id.type_password);
        mReTypePwdEditText= view.findViewById(R.id.retype_password);

        Bundle bundle = getArguments();
        String title = bundle.getString("title",getResources().getString(R.string.please_set_password));

        mTitileTextView.setText(title);
        mOkButton.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok_action:
                if (getTargetFragment()== null){
                    return;
                }
                Intent intent= new Intent();
                intent.putExtra(PASSWORD1, mTypePwdEditText.getText().toString());
                intent.putExtra(PASSWORD2, mReTypePwdEditText.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                break;
        }
    }
}
