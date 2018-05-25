package io.xdag.common.tool;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import io.xdag.common.R;

public class RefreshDelegate {

    private static final int REFRESH_TIME = 1800;

    private SwipeRefreshLayout mRefreshLayout;
    private FrameLayout mRefreshContent;
    private final OnRefreshListener mOnRefreshListener;
    private boolean mRefreshEnabled = false;


    public interface OnRefreshListener {
        void onRefresh();
    }


    public RefreshDelegate(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }


    public void attach(View rootView) {
        mRefreshLayout = rootView.findViewById(R.id.refresh);
        mRefreshContent = rootView.findViewById(R.id.refresh_content);
        initRefresh();
    }


    private void initRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mOnRefreshListener.onRefresh();
                }
            });
        }
    }


    public void setRefreshEnabled(boolean refreshEnabled) {
        if (mRefreshLayout == null) {
            return;
        }
        mRefreshEnabled = refreshEnabled;
        mRefreshLayout.setEnabled(mRefreshEnabled);
    }


    public boolean isRefreshEnabled() {
        return mRefreshEnabled;
    }


    public FrameLayout getRefreshContent() {
        return mRefreshContent;
    }

}
