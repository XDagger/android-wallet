package io.xdag.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import butterknife.Unbinder;
import io.xdag.common.util.InputMethodUtil;

/**
 * created by lxm  on 2017/12/15
 * <p>
 * desc :
 */

public class BaseDialogFragment extends DialogFragment {

    protected Activity mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parseArguments(getArguments());
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        InputMethodUtil.hideSoftInput(mContext);
    }


    protected void parseArguments(Bundle arguments) {
    }

}
