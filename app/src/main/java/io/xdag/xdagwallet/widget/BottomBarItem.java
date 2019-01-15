package io.xdag.xdagwallet.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.xdag.xdagwallet.R;

@SuppressLint("ViewConstructor")
public class BottomBarItem extends FrameLayout {
    private static final int DEFAULT_ICON_UNSELECTED = -1;
    private ImageView mIconImageView;
    private int mIconUnSelected;
    private int mIcon;
    private TextView mTvTitle;
    private Context mContext;
    private int mTabPosition = -1;

    private TextView mTvUnreadCount;

    public BottomBarItem(Context context, @DrawableRes int icon, CharSequence title) {
        this(context, icon, DEFAULT_ICON_UNSELECTED, title);
    }

    public BottomBarItem(Context context, @DrawableRes int icon, @DrawableRes
        int iconUnselected, CharSequence title) {
        super(context, null, 0);
        init(context, icon, iconUnselected, title);
    }


    private void init(Context context, int icon, int iconUnselected, CharSequence title) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackground(drawable);
        typedArray.recycle();

        LinearLayout lLContainer = new LinearLayout(context);
        lLContainer.setOrientation(LinearLayout.VERTICAL);
        lLContainer.setGravity(Gravity.CENTER);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsContainer.gravity = Gravity.CENTER;
        lLContainer.setLayoutParams(paramsContainer);

        mIcon = icon;
        mIconUnSelected = iconUnselected;
        mIconImageView = new ImageView(context);
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        if (isColorFilterMode()) {
            mIconImageView.setImageResource(icon);
            mIconImageView.setColorFilter(ContextCompat.getColor(context, R.color.bottom_bar_un_selected));
        } else {
            mIconImageView.setImageResource(mIconUnSelected);
        }
        mIconImageView.setLayoutParams(params);
        lLContainer.addView(mIconImageView);
        mTvTitle = new TextView(context);
        mTvTitle.setText(title);
        LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTv.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        mTvTitle.setTextSize(12);
        mTvTitle.setTextColor(ContextCompat.getColor(context, R.color.bottom_bar_un_selected));
        mTvTitle.setLayoutParams(paramsTv);
        lLContainer.addView(mTvTitle);

        addView(lLContainer);

        int min = dip2px(context, 20);
        int padding = dip2px(context, 5);
        mTvUnreadCount = new TextView(context);
        mTvUnreadCount.setBackgroundResource(R.drawable.bg_msg_bubble);
        mTvUnreadCount.setMinWidth(min);
        mTvUnreadCount.setTextColor(Color.WHITE);
        mTvUnreadCount.setPadding(padding, 0, padding, 0);
        mTvUnreadCount.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams tvUnReadParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, min);
        tvUnReadParams.gravity = Gravity.CENTER;
        tvUnReadParams.leftMargin = dip2px(context, 17);
        tvUnReadParams.bottomMargin = dip2px(context, 14);
        mTvUnreadCount.setLayoutParams(tvUnReadParams);
        mTvUnreadCount.setVisibility(GONE);

        addView(mTvUnreadCount);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (isColorFilterMode()) {
            if (selected) {
                mIconImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary));
                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            } else {
                mIconImageView.setColorFilter(ContextCompat.getColor(mContext, R.color.bottom_bar_un_selected));
                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.bottom_bar_un_selected));
            }
        } else {
            mIconImageView.clearColorFilter();
            if (selected) {
                mIconImageView.setImageResource(mIcon);
                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.bottom_bar_selected));
            } else {
                mIconImageView.setImageResource(mIconUnSelected);
                mTvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.bottom_bar_un_selected));
            }
        }
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    /**
     * 设置未读数量
     */
    public void setUnreadCount(int num) {
        if (num <= 0) {
            mTvUnreadCount.setText(String.valueOf(0));
            mTvUnreadCount.setVisibility(GONE);
        } else {
            mTvUnreadCount.setVisibility(VISIBLE);
            if (num > 99) {
                mTvUnreadCount.setText("99+");
            } else {
                mTvUnreadCount.setText(String.valueOf(num));
            }
        }
    }

    /**
     * 获取当前未读数量
     */
    public int getUnreadCount() {
        int count = 0;
        if (TextUtils.isEmpty(mTvUnreadCount.getText())) {
            return count;
        }
        if (mTvUnreadCount.getText().toString().equals("99+")) {
            return 99;
        }
        try {
            count = Integer.valueOf(mTvUnreadCount.getText().toString());
        } catch (Exception ignored) {
        }
        return count;
    }

    private boolean isColorFilterMode() {
        return mIconUnSelected == DEFAULT_ICON_UNSELECTED;
    }

    private int dip2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
