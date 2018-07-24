package io.xdag.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import butterknife.Unbinder;
import io.xdag.common.util.InputMethodUtil;

/**
 * created by lxm  on 2017/12/15
 * <p>
 * desc :
 */

public class BaseDialogFragment extends DialogFragment {

    protected static final String ARGS_MSG = "args_msg";
    protected static final String ARGS_CANCELABLE = "args_cancelable";
    protected Activity mContext;
    protected String mMessage;
    protected boolean mCancelable;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = getArguments().getString(ARGS_MSG);
            mCancelable = getArguments().getBoolean(ARGS_CANCELABLE);
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


    @Override public void dismiss() {
        if (isAdded()) {
            super.dismiss();
        }
    }


    public void show(FragmentManager manager) {
        if (!isAdded()) {
            super.show(manager, getClass().getSimpleName());
        }
    }


    protected static Bundle getBaseBundle(String message, boolean cancelable) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_MSG, message);
        bundle.putBoolean(ARGS_CANCELABLE, cancelable);
        return bundle;
    }
}
