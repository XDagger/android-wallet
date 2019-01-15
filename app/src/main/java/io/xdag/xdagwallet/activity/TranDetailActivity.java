package io.xdag.xdagwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import io.reactivex.disposables.CompositeDisposable;
import io.xdag.common.base.ListActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.net.HttpRequest;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import java.util.List;

/**
 * created by lxm on 2018/7/26.
 */
public class TranDetailActivity extends ListActivity<BlockDetailModel.BlockAsAddress> {

    private static final String EXTRA_ADDRESS = "extra_address";
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private String mAddress;


    @Override
    protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        mAddress = intent.getStringExtra(EXTRA_ADDRESS);
    }


    @Override
    protected int getItemLayout() {
        return R.layout.item_transaction;
    }


    @Override
    protected void initData() {
        super.initData();
        requestTranDetail(false);
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        requestTranDetail(true);
    }


    private void requestTranDetail(final boolean alert) {

        mDisposable.add(
            HttpRequest.get().getTransactionDetail(mContext, mAddress,
                blockAsAddresses -> showTransaction(blockAsAddresses, alert))
        );
    }


    private void showTransaction(List<BlockDetailModel.BlockAsAddress> blockAsAddresses, boolean alert) {
        mAdapter.setNewData(blockAsAddresses);
        if (alert) {
            AlertUtil.show(mContext, R.string.success_refresh);
        }
    }


    @Override
    protected void convert(BaseViewHolder helper, final BlockDetailModel.BlockAsAddress item) {
        super.convert(helper, item);
        helper.setText(R.id.item_transaction_tv_address, item.address);
        helper.setText(R.id.item_transaction_tv_amount, item.getAmount());
        helper.setTextColor(R.id.item_transaction_tv_amount, item.getAmountColor());
        helper.setImageResource(R.id.item_transaction_img_type, item.getTypeImage());

        TextView tvTime = helper.getView(R.id.item_transaction_tv_time);
        tvTime.setVisibility(item.getTimeVisible());
        tvTime.setText(item.time);

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranDetailActivity.start(mContext, item.address);
            }
        });

        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mContext != null) {
                    CopyUtil.copyAddress(mContext, item.address);
                } else {
                    CopyUtil.copyAddress(item.address);
                }
                return true;
            }
        });
    }


    @Override
    public void onDestroy() {
        RxUtil.dispose(mDisposable);
        super.onDestroy();
    }


    public static void start(Context context, String address) {
        Intent intent = new Intent(context, TranDetailActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        context.startActivity(intent);
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.tran_detail;
    }


    @Override
    protected int getToolbarMode() {
        return ToolbarMode.MODE_BACK;
    }
}
