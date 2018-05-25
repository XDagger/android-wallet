package io.xdag.xdagwallet.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.xdag.common.base.BaseListFragment;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.model.TransactionModel;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SettingFragment extends BaseListFragment<TransactionModel> {

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManger() {
        return new LinearLayoutManager(mContext);
    }

    @NonNull
    @Override
    protected BaseQuickAdapter<TransactionModel, BaseViewHolder> createAdapter() {
        return new TransactionAdapter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Toolbar toolbar = ((ToolbarActivity) mContext).mToolbar;
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.setting);

        }
    }

    @Override
    protected boolean isRefresh() {
        return false;
    }
}
