package io.xdag.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.xdag.common.R;

/**
 * created by ssyijiu  on 2018/7/29
 */
public abstract class ListActivity<T> extends RefreshActivity {

    protected BaseQuickAdapter<T, BaseViewHolder> mAdapter;
    protected RecyclerView mRecyclerView;

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_recycler;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(getLayoutManger());
        mAdapter = createAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @NonNull
    protected BaseQuickAdapter<T, BaseViewHolder> createAdapter() {
        return new BaseQuickAdapter<T, BaseViewHolder>(getItemLayout(), null) {
            @Override
            protected void convert(BaseViewHolder helper, T item) {
                ListActivity.this.convert(helper, item);
            }
        };
    }


    protected RecyclerView.LayoutManager getLayoutManger() {
        return new LinearLayoutManager(mContext);
    }


    protected int getItemLayout() {
        return 0;
    }


    protected void convert(BaseViewHolder helper, T item) {
    }
}
