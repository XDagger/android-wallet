package io.xdag.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.xdag.common.R;


public class XdagProgressDialog extends Dialog {
    ImageView imageView;
    TextView loadingText;
    AnimationDrawable animationDrawable;
    WeakReference<Context> context;

    public XdagProgressDialog(Context context) {
        this(context, R.style.XdagProgressDialog);
        this.context = new WeakReference(context);
    }

    public XdagProgressDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.xdagprogressdialog);
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            imageView = (ImageView) findViewById(R.id.loadingImageView);
            animationDrawable = (AnimationDrawable) imageView.getBackground();
            if (animationDrawable != null && !animationDrawable.isRunning()) {
                animationDrawable.start();
            }
        }
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        super.dismiss();
    }

    public void setLoadingText(String setLoadingText) {
        loadingText = (TextView) findViewById(R.id.loadingText);
        loadingText.setText(setLoadingText);
    }

    @Override
    public void show() {
        if (context.get() != null && context.get() instanceof Activity) {
            if (!((Activity) context.get()).isFinishing()) {
                super.show();
            }
        }
    }
}