package io.xdag.xdagwallet.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import io.xdag.common.base.AlertBuilder;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/24.
 */
public class InputBuilder extends AlertBuilder {

    private EditText mEtInput;

    public InputBuilder(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        View view = View.inflate(getContext(), R.layout.layout_dialog_input, null);
        setView(view);
        setCancelable(false);
        setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
        mEtInput = view.findViewById(R.id.dialog_input_et);
    }
}
