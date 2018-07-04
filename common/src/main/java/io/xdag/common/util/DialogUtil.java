package io.xdag.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import junit.framework.Assert;

import io.xdag.common.R;

public class DialogUtil {
    private static final String TAG = DialogUtil.class.getSimpleName();

    private static OnLeftListener leftListener = null;
    private static OnRightListener rightListener = null;
    private static OnCancelListener cancelListener = null;
    private static AlertDialog mAlertDialog;

    //the loading dialog
    private static XdagLoadingDialog mXdagLoadingDialog;

    private static XdagProgressDialog mXdagProgressDialog;

    public interface OnLeftListener {
        public void onClick();
    }

    public interface OnRightListener {
        public void onClick();
    }

    public static void setLeftListener(OnLeftListener leftListener) {
        DialogUtil.leftListener = leftListener;
    }

    public static void setRightListener(OnRightListener rightListener) {
        DialogUtil.rightListener = rightListener;
    }

    public static void setCancelListener(
            OnCancelListener cancelListener) {
        DialogUtil.cancelListener = cancelListener;
    }

    /**
     * show alert dialog
     *
     * @param context  context
     * @param titleMsg title
     * @param tipMsg   text on right of the dialog
     */
    public static void showAlertDialog(Context context, String titleMsg,
                                       String tipMsg) {
        showAlertDialog(context, titleMsg, null, null, tipMsg, false, false);
    }

    /**
     * show alert dialog
     *
     * @param context  context
     * @param titleMsg title
     * @param tipMsg   text on right of the dialog
     * @param btnMsg1  text of left button
     * @param btnMsg2  text of right button
     */
    public static void showAlertDialog(Context context, String titleMsg,
                                       String tipMsg, String btnMsg1, String btnMsg2) {
        showAlertDialog(context, titleMsg, btnMsg1, btnMsg2, tipMsg, false, false);
    }

    /**
     * show alert dialog
     *
     * @param context  context
     * @param titleMsg title
     * @param tipMsg   text on right of the dialog
     * @param btnMsg1  text of left button
     * @param btnMsg2  text of right button
     * @param bool     is cancel this dialog is allowed(true: can't，false: can)
     */
    public static void showAlertDialog(Context context, String titleMsg,
                                       String tipMsg, String btnMsg1, String btnMsg2, boolean bool) {
        showAlertDialog(context, titleMsg, btnMsg1, btnMsg2, tipMsg, false, bool);
    }

    /**
     * show alert dialog
     *
     * @param context        context
     * @param titleMsg       title
     * @param btnMsg1        text of left button
     * @param btnMsg2        text of right button
     * @param tipMsg         notice message
     * @param isHtml         is show message with html format
     * @param isCanNotCancel is cancel this dialog is allowed(true: can't，false: can)
     */
    public static void showAlertDialog(Context context, String titleMsg,
                                       String btnMsg1, String btnMsg2, String tipMsg, boolean isHtml,
                                       boolean isCanNotCancel) {
        try {
            showDialogCatchException(context, titleMsg, btnMsg1, btnMsg2, tipMsg, isHtml, isCanNotCancel, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showAlertDialog(Context context, String titleMsg, String btnMsg1, String btnMsg2, String tipMsg, boolean isHtml, boolean isCanNotCancel, boolean textGravityCenter) {
        try {
            showDialogCatchException(context, titleMsg, btnMsg1, btnMsg2, tipMsg, isHtml, isCanNotCancel, textGravityCenter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void showDialogCatchException(Context context,
                                                 String titleMsg, String btnMsg1, String btnMsg2, String tipMsg,
                                                 boolean isHtml, boolean isAllowCancel, boolean textGravityCenter) {
        if (mAlertDialog != null && mAlertDialog.isShowing() && context != null) {
            if (mAlertDialog.getContext() == context) {
                return;
            } else {
            }
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        mAlertDialog = new AlertDialog(context);
        mAlertDialog.setTitle(titleMsg);
        mAlertDialog.setIsAllowCancel(isAllowCancel);

        if (!StringUtil.isEmpty(titleMsg)) {
            mAlertDialog.setTitle(titleMsg);
        }

        if (textGravityCenter) {
            mAlertDialog.getContent().setGravity(Gravity.CENTER_HORIZONTAL);
        }

        mAlertDialog.setMessage(tipMsg, isHtml);

        if (!TextUtils.isEmpty(btnMsg2)) {
            //change the button layout when both button is setted
            mAlertDialog.setBtn2Visible(true);
            if (!StringUtil.isEmpty(btnMsg1)) {
                mAlertDialog.setBtn2Text(btnMsg1);
            }
            mAlertDialog.setBtn2ClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leftListener != null) {
                        leftListener.onClick();
                    }
                    leftListener = null;
                    rightListener = null;
                }
            });
            mAlertDialog.setBtn1Text(btnMsg2);
            mAlertDialog.setBtn1ClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rightListener != null) {
                        rightListener.onClick();
                    }
                    rightListener = null;
                    leftListener = null;
                }
            });
        } else { //keep logic while singal button
            mAlertDialog.setBtn2Visible(false);
            if (!StringUtil.isEmpty(btnMsg1)) {
                mAlertDialog.setBtn1Text(btnMsg1);
            }
            mAlertDialog.setBtn1ClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leftListener != null) {
                        leftListener.onClick();
                    }
                    leftListener = null;
                    rightListener = null;
                }
            });
        }

        mAlertDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelListener != null) {
                    cancelListener.onCancel(dialog);
                }
                cancelListener = null;
                leftListener = null;
                rightListener = null;

            }
        });

        mAlertDialog.show();
    }


    /**
     * show loading dialog
     */
    public static void showLoadingDialog(Context context, String loadingtxt,
                                         boolean cancelAble) {
        if (mXdagProgressDialog != null) {
            if (mXdagProgressDialog.isShowing())
                return;
        }
        if (mXdagProgressDialog != null && mXdagProgressDialog.isShowing()
                && context != null) {
            if (mXdagProgressDialog.getContext() == context) {
                return;
            } else {
                mXdagProgressDialog.dismiss();
            }
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        mXdagProgressDialog = new XdagProgressDialog(context);
        mXdagProgressDialog.setTitle(R.string.dialog_defalut_title);
        mXdagProgressDialog.setCancelable(cancelAble);
        //show the dialog
        mXdagProgressDialog.show();
    }

    /**
     * hide loading dialog
     */
    public static void dismissLoadingDialog() {
        try {
            if (mXdagProgressDialog != null
                    && mXdagProgressDialog.isShowing()) {
                mXdagProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dialog is show
     */
    public static boolean isShow() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * hide alert dialog
     */
    public static void dismissAlertDialog() {
        try {
            if (mAlertDialog != null) {
                // IllegalArgumentException
                mAlertDialog.dismiss();
            }
        } catch (Exception e) {
        }

    }

    /**
     * show network settings dialog
     * @param activity
     */
    public static void showNetWorkSetingDialog(final Activity activity) {
        Assert.assertNotNull(activity);
        DialogUtil.showAlertDialog(activity,
                activity.getString(R.string.dialog_defalut_title),
                activity.getString(R.string.network_is_unavailable),
                activity.getString(R.string.alert_dialog_cancel),
                activity.getString(R.string.settings));
        DialogUtil.setRightListener(new DialogUtil.OnRightListener() {

                    @Override
                    public void onClick() {
                        Intent intent = new Intent();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            intent = new Intent(
                                    android.provider.Settings.ACTION_SETTINGS);
                        } else {
                            intent.setClassName("com.android.settings",
                                    "com.android.settings.WirelessSettings");
                            //can not find activity in android 4.0
                        }
                        activity.startActivity(intent);
                    }
                });
    }

}
