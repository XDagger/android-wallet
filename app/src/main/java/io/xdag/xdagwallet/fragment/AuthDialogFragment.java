package io.xdag.xdagwallet.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.xdag.xdagwallet.R;

public class AuthDialogFragment extends DialogFragment {

    private EditText tvAuthInfo = null;
    private AuthInputListener mListener;

    public interface AuthInputListener
    {
        void onAuthInputComplete(String authInfo);
    }

    public void showAuthDialog(View view)
    {
        AuthDialogFragment dialog = new AuthDialogFragment();
        dialog.show(getFragmentManager(), "Auth Dialog");
    }

    public void setAuthHintInfo(String hintInfo) {
        if(tvAuthInfo != null){
            tvAuthInfo.setHint(hintInfo);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_authentication, null);
        tvAuthInfo = (EditText) view.findViewById(R.id.txt_auth_info);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                AuthInputListener listener = (AuthInputListener) getActivity();
                                listener.onAuthInputComplete(tvAuthInfo.getText().toString());
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AuthInputListener) {
            mListener = (AuthInputListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement listener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }
}
