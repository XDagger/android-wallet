package io.xdag.xdagwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.api.ApiServer;
import io.xdag.xdagwallet.api.xdagscan.BlockDetailModel;
import io.xdag.xdagwallet.api.xdagscan.Detail2TranListFunction;
import io.xdag.xdagwallet.api.xdagscan.ErrorConsumer;
import io.xdag.xdagwallet.util.RxUtil;
import java.util.List;

/**
 * created by lxm on 2018/7/26.
 */
public class TranDetailActivity extends ToolbarActivity {

    private static final String EXTRA_ADDRESS = "extra_address";
    private TransactionAdapter mAdapter;
    private Disposable mDisposable;
    private String mAddress;

    @BindView(R.id.tran_detail_rv) RecyclerView mRecyclerView;


    @Override protected int getLayoutResId() {
        return R.layout.activity_tran_detail;
    }


    @Override protected void initView(View rootView, Bundle savedInstanceState) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // View headerView = new TranDetailHeaderView(mContext);
        // mAdapter.addHeaderView(headerView);
    }


    @Override protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        mAddress = intent.getStringExtra(EXTRA_ADDRESS);
    }


    @Override protected void initData() {
        super.initData();
        requestTranDetail();
    }


    private void requestTranDetail() {

        mDisposable = ApiServer.getApi().getBlockDetail(mAddress)
            .observeOn(AndroidSchedulers.mainThread())
            .map(new Detail2TranListFunction())
            .subscribe(new Consumer<List<BlockDetailModel.BlockAsAddress>>() {
                @Override
                public void accept(List<BlockDetailModel.BlockAsAddress> blockAsAddresses) {
                    if (mAdapter == null) {
                        mAdapter = new TransactionAdapter(blockAsAddresses);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.setNewData(blockAsAddresses);
                    }
                }
            }, new ErrorConsumer(mContext));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }


    public static void start(Context context, String address) {
        Intent intent = new Intent(context, TranDetailActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        context.startActivity(intent);
    }


    @Override protected int getToolbarTitle() {
        return R.string.tran_detail;
    }


    @Override protected int getToolbarMode() {
        return ToolbarMode.MODE_BACK;
    }
}
