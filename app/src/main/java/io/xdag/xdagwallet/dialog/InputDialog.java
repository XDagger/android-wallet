package io.xdag.xdagwallet.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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


    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_input, null);
        TextView tvLoading = view.findViewById(R.id.dialog_loading_tv);

        tvLoading.setText(mMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
            .setView(view);
        setCancelable(mCancelable);
        return builder.create();

    }
}
