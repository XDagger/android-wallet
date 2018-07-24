package io.xdag.xdagwallet.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.xdag.common.base.BaseDialogFragment;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/24.
 */
public class InputDialog extends BaseDialogFragment {


    public static InputDialog newInstance(String message, boolean cancelable) {
        InputDialog dialog = new InputDialog();
        dialog.setArguments(getBaseBundle(message, cancelable));
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_input, null);
        EditText etInput = view.findViewById(R.id.dialog_input_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setView(view)
                .setMessage(mMessage)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        setCancelable(mCancelable);
        return builder.create();

    }

    public static InputDialog show(String message, boolean cancelable, FragmentManager fragmentManager) {
        InputDialog dialog = newInstance(message, cancelable);
        dialog.show(fragmentManager);
        return dialog;
    }
}
