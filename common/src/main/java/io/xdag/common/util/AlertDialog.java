package io.xdag.common.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.xdag.common.R;

public class AlertDialog extends Dialog implements View.OnClickListener{
    private TextView tvTitle;
    private TextView mContent;
    private EditText editText;
    private Button btn1;
    private Button btn2;

    private View.OnClickListener mBtn1ClickListener;
    private View.OnClickListener mBtn2ClickListener;

    private boolean isAutoDismiss1 = true;
    private boolean isAutoDismiss2 = true;

    //true user
    private boolean isAllowCancel = false;

    public AlertDialog(Context context) {
        super(context);
        init();
    }

    private void init() {

        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        super.setContentView(R.layout.layout_alert_dialog);
        tvTitle = (TextView) findViewById(R.id.alert_title);
        mContent = (TextView) findViewById(R.id.alert_content);
        editText = (EditText) findViewById(R.id.alert_edit_content);

        btn1 = (Button) findViewById(R.id.a_btn1);
        btn2 = (Button) findViewById(R.id.a_btn2);


        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        //bold
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        Window window  = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.4f;

        int width = (int) (window.getWindowManager().getDefaultDisplay().getWidth()*0.9);
        window.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    /**
     * set dialog  title
     * @param title
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * set dialog  title
     * @param resId
     * @see Dialog#setTitle(int)
     */
    @Override
    public void setTitle(int resId) {
        setTitle(getContext().getResources().getString(resId));
    }

    /**
     * set message
     * @param text
     */
    public void setMessage(String text) {
        mContent.setText(text);
    }

    public void setMessage(String text, boolean isHtml) {
        if(isHtml){
            mContent.setText(Html.fromHtml(text));
        }else{
            mContent.setText(text);
        }
    }

    /**
     * set edit text content
     * @param text
     */
    public void setEditMessage(String text) {
        editText.setText(text);
    }

    /**
     * get edit text content
     */
    public String getEditMessage() {
        return editText.getText().toString();
    }

    /**
     * set edit text object
     * @param
     */
    public EditText getEditObject() {
        return editText;
    }

    /**
     * set edit text visiable
     * @param visible
     */
    public void setEditShow(boolean visible) {
        if(visible){
            editText.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        }else{
            editText.setVisibility(View.GONE);
            mContent.setVisibility(View.VISIBLE);
        }
    }

    public void setEditPwdMode(int  type){
        editText.setInputType(type);
    }

    /**
     * set button1 text
     * @param text
     */
    public void setBtn1Text(String text) {
        btn1.setText(text);
    }

    /**
     * set button2 text
     * @param text
     */
    public void setBtn2Text(String text) {
        btn2.setText(text);
    }


    /**
     * set button1 text
     * @param resId
     */
    public void setBtn1Text(int resId) {
        btn1.setText(resId);
    }

    /**
     * set button2 text
     * @param resId
     */
    public void setBtn2Text(int resId) {
        btn2.setText(resId);
    }


    /**
     * set dialog is hidden after button1 is clicked
     * default: true
     * @param autoDismiss
     */
    public void setAutoDismiss1(boolean autoDismiss) {
        isAutoDismiss1 = autoDismiss;
    }

    /**
     * set dialog is hidden after button2 is clicked
     * default: true
     * @param autoDismiss
     */
    public void setAutoDismiss2(boolean autoDismiss) {
        isAutoDismiss2 = autoDismiss;
    }

    /**
     * set button1 visible
     * @param visible
     */
    public void setBtn1Visible(boolean visible) {
        if (visible) {
            btn1.setVisibility(View.VISIBLE);
        } else {
            btn1.setVisibility(View.GONE);
        }
    }

    /**
     * set button2 visible
     * @param visible
     */
    public void setBtn2Visible(boolean visible) {
        if (visible) {
            btn2.setVisibility(View.VISIBLE);
        } else {
            btn2.setVisibility(View.GONE);
        }
    }


    /**
     *
     * set button1 enable
     * @param enabled
     */
    public void setBtn1Enable(boolean enabled){
        btn1.setEnabled(enabled);
    }

    /**
     *
     * set button2 enable
     * @param enabled
     */
    public void setBtn2Enable(boolean enabled){
        btn2.setEnabled(enabled);
    }


    /**
     * @param listener
     */
    public void setBtn1ClickListener(View.OnClickListener listener) {
        mBtn1ClickListener = listener;
    }

    /**
     * @param listener
     */
    public void setBtn2ClickListener(View.OnClickListener listener) {
        mBtn2ClickListener = listener;
    }

    public TextView getContent() {
        return mContent;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.a_btn1) {
            if (mBtn1ClickListener != null) {
                mBtn1ClickListener.onClick(v);
            }
            if (isAutoDismiss1) {
                dismiss();
            }

        } else if (id == R.id.a_btn2) {
            if (mBtn2ClickListener != null) {
                mBtn2ClickListener.onClick(v);
            }
            if (isAutoDismiss2) {
                dismiss();
            }
        } else {
        }
    }

    private void refreshUIStyle(){

    }

    /**
     * is allow user cancel the  dialog
     * @return
     */
    public boolean isAllowCancel() {
        return isAllowCancel;
    }
    /**
     *
     * @param canNotCancel
     */
    public void setIsAllowCancel(boolean canNotCancel) {
        this.isAllowCancel = canNotCancel;
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
}
