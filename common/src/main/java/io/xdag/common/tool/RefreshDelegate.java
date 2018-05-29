package io.xdag.common.tool;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;

import io.xdag.common.R;

public class RefreshDelegate {

    private static final int REFRESH_TIME = 1800;
    private final OnRefreshListener mOnRefreshListener;
    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private FrameLayout mRefreshContent;


    public RefreshDelegate(Context context, OnRefreshListener listener) {
        mOnRefreshListener = listener;
        mRootView = View.inflate(context, R.layout.layout_refresh, null);
        mRefreshLayout = mRootView.findViewById(R.id.refresh);
        mRefreshContent = mRootView.findViewById(R.id.refresh_content);
        initRefresh();
    }


    private void initRefresh() {
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mOnRefreshListener.onRefresh();
                    setRefreshing(false);
                }
            });

        }
    }


    private void setRefreshing(final boolean refreshing) {
        if (mRefreshLayout != null) {
            if (refreshing) {
                mRefreshLayout.setRefreshing(true);
            } else {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }
                }, REFRESH_TIME);
            }
        }

    }


    public void setRefreshEnabled(boolean refreshEnabled) {
        if (mRefreshLayout != null) {
            mRefreshLayout.setEnabled(refreshEnabled);
        }
    }


    public View getRootView() {
        return mRootView;
    }


    public FrameLayout getContent() {
        return mRefreshContent;
    }


    public interface OnRefreshListener {
        void onRefresh();
    }

}
