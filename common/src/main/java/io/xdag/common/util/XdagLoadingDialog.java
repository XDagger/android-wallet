package io.xdag.common.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.xdag.common.R;

public class XdagLoadingDialog extends Dialog implements View.OnClickListener
{

    private TextView loadingtxt;
    private OnCancleListener onCancleListener;

    Context mContext;

    private boolean isAllowCancel = false;


    public XdagLoadingDialog(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    private void init() {

        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        super.setContentView(R.layout.layout_loading_dialog);

        loadingtxt = (TextView)this.findViewById(R.id.txt);

        //show loading


        Window window  = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.5f;

        //int width = (int) (window.getWindowManager().getDefaultDisplay().getWidth()*0.9);
        window.setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    /**
     * set loading dialog text
     * @param txt
     */
    public void setLoadingTxt(String txt) {
        loadingtxt.setText(txt);
    }

    /**
     * set loading dialog text
     * @param resId
     * @see Dialog#setTitle(int)
     */
    public void setLoadingTxt(int resId) {
        setLoadingTxt(getContext().getResources().getString(resId));
    }

    private void refreshUIStyle(){

    }

    /**
     * is allow cancel
     * @return
     */
    public boolean isAllowCancel() {
        return isAllowCancel;
    }
    /**
     * set is allow cancel
     * @param allowCancel
     */
    public void setAllowCancel(boolean allowCancel) {
        this.isAllowCancel = allowCancel;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
            if (!isAllowCancel) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    /**
     * callback on cancel
     */
    public interface OnCancleListener {
        public void onCancle();
    }

    /**
     * set cancel listener
     */
    public void setOnCancleListener(
            OnCancleListener onCancleListener) {
        this.onCancleListener = onCancleListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(onCancleListener!=null){
            onCancleListener.onCancle();
        }
    }

}

